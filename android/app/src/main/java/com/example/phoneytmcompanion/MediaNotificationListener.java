package com.example.phoneytmcompanion;
import android.content.ComponentName; import android.media.session.*; import android.service.notification.NotificationListenerService;
import java.io.*; import java.net.*; import java.util.*;
public class MediaNotificationListener extends NotificationListenerService {
 static MediaNotificationListener instance; static ServerSocket server;
 public void onListenerConnected(){super.onListenerConnected();instance=this;startServer();}
 public void onListenerDisconnected(){instance=null;super.onListenerDisconnected();}
 static void startServer(){new Thread(()->{try{if(server!=null&&!server.isClosed())return;server=new ServerSocket(8765);while(!server.isClosed()){Socket s=server.accept();new Thread(()->handle(s)).start();}}catch(Exception ignored){}}).start();}
 static MediaController controller(){try{if(instance==null)return null; MediaSessionManager m=(MediaSessionManager)instance.getSystemService(MEDIA_SESSION_SERVICE);
  List<MediaController> ss=m.getActiveSessions(new ComponentName(instance,MediaNotificationListener.class)); for(MediaController c:ss)if("com.google.android.apps.youtube.music".equals(c.getPackageName()))return c; return ss.isEmpty()?null:ss.get(0);}catch(Exception e){return null;}}
 static String jsonState(){MediaController c=controller();if(c==null)return "{\"connected\":true,\"playing\":false,\"title\":\"\",\"artist\":\"\"}";
  MediaMetadata m=c.getMetadata();PlaybackState p=c.getPlaybackState();String title=m==null?"":String.valueOf(m.getText(MediaMetadata.METADATA_KEY_TITLE));String artist=m==null?"":String.valueOf(m.getText(MediaMetadata.METADATA_KEY_ARTIST));long pos=p==null?0:p.getPosition();long dur=m==null?0:m.getLong(MediaMetadata.METADATA_KEY_DURATION);boolean play=p!=null&&p.getState()==PlaybackState.STATE_PLAYING;
  return "{\"connected\":true,\"playing\":"+play+",\"title\":\""+esc(title)+"\",\"artist\":\""+esc(artist)+"\",\"position\":"+pos+",\"duration\":"+dur+"}";}
 static String cmd(String x){MediaController c=controller();if(c==null)return "{\"ok\":false,\"error\":\"no active media session\"}";try{MediaController.TransportControls t=c.getTransportControls();if(x.equals("next"))t.skipToNext();else if(x.equals("previous"))t.skipToPrevious();else {PlaybackState p=c.getPlaybackState();if(p!=null&&p.getState()==PlaybackState.STATE_PLAYING)t.pause();else t.play();}return "{\"ok\":true}";}catch(Exception e){return "{\"ok\":false}";}}
 static void handle(Socket s){try{BufferedReader r=new BufferedReader(new InputStreamReader(s.getInputStream()));String q=r.readLine();String path=q==null?"/":q.split(" ")[1];String body=path.startsWith("/api/state")?jsonState():path.startsWith("/api/next")?cmd("next"):path.startsWith("/api/previous")?cmd("previous"):path.startsWith("/api/playpause")?cmd("playpause"):"Phone YTM Companion OK";byte[] d=body.getBytes("UTF-8");OutputStream o=s.getOutputStream();o.write(("HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: "+d.length+"\r\nConnection: close\r\n\r\n").getBytes());o.write(d);o.close();}catch(Exception ignored){}}
 static String esc(String x){return x==null?"":x.replace("\\","\\\\").replace("\"","\\\"");}
}