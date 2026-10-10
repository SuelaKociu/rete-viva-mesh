// Copyright (c) 2026 Suela Kociu. SPDX-License-Identifier: MPL-2.0
package it.reteviva.mesh;
public final class MeshRules {
 private MeshRules() {}
 public static final int INITIAL_TTL=6;
 public static final long MAX_AGE_MS=24L*60*60*1000;
 public static boolean canRelay(int ttl,long time,long now) {
   return ttl>0 && ttl<=INITIAL_TTL && time>0 && time<=now+60000 && now-time<=MAX_AGE_MS;
 }
 public static int afterHop(int ttl) { return Math.max(0,ttl-1); }
}
