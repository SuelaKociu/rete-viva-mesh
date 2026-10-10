// Copyright (c) 2026 Suela Kociu. SPDX-License-Identifier: MPL-2.0
package it.reteviva.mesh;
import org.junit.Test;
import static org.junit.Assert.*;
public class MeshRulesTest {
 @Test public void ttlExpires(){assertEquals(5,MeshRules.afterHop(6));assertEquals(0,MeshRules.afterHop(0));assertFalse(MeshRules.canRelay(0,1000,2000));}
 @Test public void ageExpires(){long now=200000000L;assertTrue(MeshRules.canRelay(3,now-1000,now));assertFalse(MeshRules.canRelay(3,now-MeshRules.MAX_AGE_MS-1,now));assertFalse(MeshRules.canRelay(3,now+61000,now));}
}
