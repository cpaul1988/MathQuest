const fs=require('fs'),assert=require('node:assert/strict');
const {initializeTestEnvironment,assertSucceeds,assertFails}=require('@firebase/rules-unit-testing');
const {doc,setDoc,getDoc,updateDoc,deleteDoc,collection,getDocs,serverTimestamp}=require('firebase/firestore');
(async()=>{
 const env=await initializeTestEnvironment({projectId:'demo-mathquest',firestore:{rules:fs.readFileSync(require('path').join(__dirname,'../firebase/firestore.rules'),'utf8')}});
 try{
  const now=Math.floor(Date.now()/1000),owner=env.authenticatedContext('parent-a',{email_verified:true,auth_time:now}).firestore();
  const other=env.authenticatedContext('parent-b',{email_verified:true,auth_time:now}).firestore();
  const unverified=env.authenticatedContext('parent-a',{email_verified:false,auth_time:now}).firestore();
  const anon=env.unauthenticatedContext().firestore();const path='households/parent-a';
  const data=(revision=1,state='active',payload='{"version":12,"profiles":[]}')=>({schema:1,revision,state,payload,updatedAt:serverTimestamp()});
  await assertFails(setDoc(doc(anon,path),data()));await assertFails(getDoc(doc(anon,path)));
  await assertFails(setDoc(doc(other,path),data()));await assertFails(getDoc(doc(other,path)));
  await assertFails(setDoc(doc(unverified,path),data()));
  await assertSucceeds(setDoc(doc(owner,path),data()));await assertSucceeds(getDoc(doc(owner,path)));
  await assertFails(getDocs(collection(owner,'households')));
  await assertFails(updateDoc(doc(owner,path),data(1)));await assertFails(updateDoc(doc(owner,path),data(3)));
  await assertFails(updateDoc(doc(owner,path),{...data(2),pin:'1234'}));
  await assertFails(updateDoc(doc(owner,path),data(2,'active','x'.repeat(650001))));
  await assertFails(updateDoc(doc(owner,path),{...data(2),updatedAt:new Date(0)}));
  await assertSucceeds(updateDoc(doc(owner,path),data(2)));
  await assertFails(deleteDoc(doc(owner,path)));
  const stale=env.authenticatedContext('parent-a',{email_verified:true,auth_time:now-3600}).firestore();
  await assertFails(updateDoc(doc(stale,path),data(3,'deleted','')));
  await assertSucceeds(updateDoc(doc(owner,path),data(3,'deleted','')));
  await assertFails(updateDoc(doc(owner,path),data(4)));await assertFails(updateDoc(doc(stale,path),data(4)));
  await assertSucceeds(updateDoc(doc(owner,path),data(4,'deleted','')));
  const newUnverified=env.authenticatedContext('unverified-new',{email_verified:false,auth_time:now}).firestore();
  await assertSucceeds(setDoc(doc(newUnverified,'households/unverified-new'),data(1,'deleted','')));
  const snap=await getDoc(doc(owner,path));assert.equal(snap.data().payload,'');
  console.log('PASS Firestore isolation, verification, schema, size, revisions, timestamps, recent-auth deletion, tombstone protection, unverified account deletion');
 }finally{await env.cleanup();}
})().catch(e=>{console.error(e);process.exit(1)});
