// Copyright (c) 2026 Suela Kociu. SPDX-License-Identifier: MPL-2.0
package it.reteviva.mesh;

import android.Manifest;
import android.app.*;
import android.os.*;
import android.content.pm.PackageManager;
import android.widget.*;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.*;
import org.json.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/** Foreground-only research demonstrator. NOT an emergency messenger. */
public final class MainActivity extends Activity {
 private static final String SERVICE_ID="it.reteviva.mesh";
 private final Set<String> peers=new CopyOnWriteArraySet<>();
 private final Set<String> pending=new HashSet<>();
 private final Set<String> seen=new HashSet<>();
 private final List<JSONObject> cache=new ArrayList<>();
 private ConnectionsClient client;
 private String nodeName;
 private boolean running=false;
 private TextView status, log;
 private EditText name, draft;
 private Button start;

 @Override public void onCreate(Bundle b){
   super.onCreate(b);
   String id=getPreferences(MODE_PRIVATE).getString("node",null);
   if(id==null){id=UUID.randomUUID().toString().substring(0,8);getPreferences(MODE_PRIVATE).edit().putString("node",id).apply();}
   nodeName="NODO-"+id;
   client=Nearby.getConnectionsClient(this);
   restore();
   LinearLayout layout=new LinearLayout(this);layout.setOrientation(1);layout.setPadding(24,24,24,24);
   ScrollView scroll=new ScrollView(this);scroll.addView(layout);setContentView(scroll);
   TextView title=new TextView(this);title.setText("RETE VIVA · Laboratorio 0.1");title.setTextSize(23);layout.addView(title);
   TextView warning=new TextView(this);warning.setText("SOLO PER TEST. Non usare per chiedere soccorso o inviare dati sensibili. Lascia aperta questa schermata.\nNodo: "+nodeName);layout.addView(warning);
   name=new EditText(this);name.setSingleLine(true);name.setHint("Nome visualizzato");name.setText("Volontario");layout.addView(name);
   start=new Button(this);start.setText("Avvia rete offline");start.setOnClickListener(v->{if(running)stop();else permissions();});layout.addView(start);
   status=new TextView(this);layout.addView(status);status("Rete ferma");
   draft=new EditText(this);draft.setHint("Messaggio di prova (max 500 caratteri)");layout.addView(draft);
   Button send=new Button(this);send.setText("Invia messaggio");send.setOnClickListener(v->publish());layout.addView(send);
   log=new TextView(this);layout.addView(log);render();
 }

 private String[] requestedPermissions(){
   if(Build.VERSION.SDK_INT>=33)return new String[]{Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_ADVERTISE,Manifest.permission.NEARBY_WIFI_DEVICES};
   if(Build.VERSION.SDK_INT>=31)return new String[]{Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_ADVERTISE,Manifest.permission.ACCESS_FINE_LOCATION};
   return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
 }
 private void permissions(){
   List<String> missing=new ArrayList<>();
   for(String p:requestedPermissions())if(checkSelfPermission(p)!=PackageManager.PERMISSION_GRANTED)missing.add(p);
   if(missing.isEmpty())begin();else requestPermissions(missing.toArray(new String[0]),20);
 }
 @Override public void onRequestPermissionsResult(int code,String[] p,int[] r){
   super.onRequestPermissionsResult(code,p,r);
   if(code!=20)return;
   boolean ok=r.length>0;for(int x:r)ok &= x==PackageManager.PERMISSION_GRANTED;
   if(ok)begin();else status("Permessi necessari non concessi");
 }
 private final EndpointDiscoveryCallback discovery=new EndpointDiscoveryCallback(){
   @Override public void onEndpointFound(String id,DiscoveredEndpointInfo info){
     // A deterministic tie-breaker prevents simultaneous connection requests.
     if(nodeName.compareTo(info.getEndpointName())>=0 || !pending.add(id))return;
     client.requestConnection(nodeName,id,lifecycle).addOnFailureListener(e->{pending.remove(id);status("Richiesta fallita");});
   }
   @Override public void onEndpointLost(String id){pending.remove(id);}
 };
 private final ConnectionLifecycleCallback lifecycle=new ConnectionLifecycleCallback(){
   @Override public void onConnectionInitiated(String id,ConnectionInfo info){
     runOnUiThread(()->new AlertDialog.Builder(MainActivity.this)
       .setTitle("Verifica i codici su entrambi i telefoni")
       .setMessage("Nodo: "+info.getEndpointName()+"\nCodice: "+info.getAuthenticationDigits()+"\nConferma SOLO se i codici coincidono su entrambi i telefoni.")
       .setPositiveButton("Coincidono", (d,w)->client.acceptConnection(id,receiver).addOnFailureListener(e->status("Connessione rifiutata")))
       .setNegativeButton("Non coincidono",(d,w)->client.rejectConnection(id))
       .setCancelable(false).show());
   }
   @Override public void onConnectionResult(String id,ConnectionResolution result){
     pending.remove(id);
     if(result.getStatus().getStatusCode()==ConnectionsStatusCodes.STATUS_OK){peers.add(id);status("Collegato");synchronize(id);}
     else status("Connessione non riuscita");
   }
   @Override public void onDisconnected(String id){peers.remove(id);pending.remove(id);status("Nodo scollegato");}
 };
 private final PayloadCallback receiver=new PayloadCallback(){
   @Override public void onPayloadReceived(String id,Payload p){
     if(p.getType()!=Payload.Type.BYTES || p.asBytes()==null || p.asBytes().length>4096)return;
     try{
       JSONObject m=new JSONObject(new String(p.asBytes(),StandardCharsets.UTF_8));
       String mid=m.optString("id"),author=m.optString("author"),body=m.optString("body");
       long when=m.optLong("time",0);int ttl=m.optInt("ttl",-1);
       if(!"msg".equals(m.optString("kind")) || mid.length()<8 || mid.length()>80 || author.length()>40 || body.isEmpty() || body.length()>500 || ttl<0 || ttl>MeshRules.INITIAL_TTL || when<=0 || when>System.currentTimeMillis()+60000 || System.currentTimeMillis()-when>MeshRules.MAX_AGE_MS)return;
       runOnUiThread(()->{if(!seen.add(mid))return;remember(m);relay(m,id);});
     }catch(Exception ignored){}
   }
   @Override public void onPayloadTransferUpdate(String id,PayloadTransferUpdate update){}
 };
 private void begin(){
   if(running)return;
   running=true;start.setText("Ferma rete");status("Avvio scoperta e annuncio...");
   Strategy s=Strategy.P2P_CLUSTER;
   client.startAdvertising(nodeName,SERVICE_ID,lifecycle,new AdvertisingOptions.Builder().setStrategy(s).build()).addOnFailureListener(e->status("Annuncio fallito: "+e.getMessage()));
   client.startDiscovery(SERVICE_ID,discovery,new DiscoveryOptions.Builder().setStrategy(s).build()).addOnFailureListener(e->status("Ricerca fallita: "+e.getMessage()));
 }
 private void stop(){
   running=false;client.stopAdvertising();client.stopDiscovery();client.stopAllEndpoints();
   peers.clear();pending.clear();if(start!=null)start.setText("Avvia rete offline");status("Rete ferma");
 }
 private void publish(){
   String body=draft.getText().toString().trim(),author=name.getText().toString().trim();
   if(body.isEmpty())return;
   if(body.length()>500){Toast.makeText(this,"Massimo 500 caratteri",Toast.LENGTH_LONG).show();return;}
   if(author.isEmpty())author="Volontario";if(author.length()>40)author=author.substring(0,40);
   try{
     JSONObject m=new JSONObject();m.put("kind","msg");m.put("id",UUID.randomUUID().toString());m.put("author",author);m.put("body",body);m.put("time",System.currentTimeMillis());m.put("ttl",MeshRules.INITIAL_TTL);
     seen.add(m.getString("id"));remember(m);relay(m,null);draft.setText("");
   }catch(Exception e){status("Errore durante invio");}
 }
 private void relay(JSONObject m,String except){
   int ttl=m.optInt("ttl");if(!MeshRules.canRelay(ttl,m.optLong("time"),System.currentTimeMillis()))return;
   try{
     JSONObject next=new JSONObject(m.toString());next.put("ttl",MeshRules.afterHop(ttl));
     byte[] bytes=next.toString().getBytes(StandardCharsets.UTF_8);if(bytes.length>4096)return;
     for(String id:peers)if(!id.equals(except))client.sendPayload(id,Payload.fromBytes(bytes));
   }catch(Exception ignored){}
 }
 private void synchronize(String id){
   for(JSONObject m:new ArrayList<>(cache)){
     if(!MeshRules.canRelay(m.optInt("ttl"),m.optLong("time"),System.currentTimeMillis()))continue;
     try{JSONObject next=new JSONObject(m.toString());next.put("ttl",MeshRules.afterHop(m.optInt("ttl")));client.sendPayload(id,Payload.fromBytes(next.toString().getBytes(StandardCharsets.UTF_8)));}catch(Exception ignored){}
   }
 }
 private void remember(JSONObject m){
   cache.add(m);while(cache.size()>100)cache.remove(0);
   JSONArray items=new JSONArray();for(JSONObject i:cache)items.put(i);
   getPreferences(MODE_PRIVATE).edit().putString("cached",items.toString()).apply();render();
 }
 private void restore(){
   try{JSONArray a=new JSONArray(getPreferences(MODE_PRIVATE).getString("cached","[]"));
     for(int i=Math.max(0,a.length()-100);i<a.length();i++){JSONObject m=a.getJSONObject(i);long age=System.currentTimeMillis()-m.optLong("time",0);if(age<0||age>MeshRules.MAX_AGE_MS)continue;cache.add(m);seen.add(m.optString("id"));}
   }catch(Exception ignored){}
 }
 private void render(){if(log==null)return;StringBuilder b=new StringBuilder("\nMESSAGGI DI PROVA\n");for(JSONObject m:cache)b.append("\n• ").append(m.optString("author")).append(": ").append(m.optString("body"));log.setText(b.toString());}
 private void status(String s){runOnUiThread(()->{if(status!=null)status.setText("Stato: "+s+"\nNodi collegati: "+peers.size());});}
 @Override protected void onDestroy(){stop();super.onDestroy();}
}
