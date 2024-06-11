import java.util.*;
import java.time.*;
import java.math.*;
public class Main {
 static void check(boolean condition){if(!condition)throw new AssertionError();}

 static class Notifications {
  final Set<List<String>> delivered=new HashSet<>();final Map<String,Deque<Instant>> windows=new HashMap<>();final int limit;final Duration window;
  Notifications(int limit,Duration window){if(limit<1||window.isNegative()||window.isZero())throw new IllegalArgumentException();this.limit=limit;this.window=window;}
  synchronized String deliver(String tenant,String event,Instant now,Runnable sender){if(tenant.isBlank()||event.isBlank())throw new IllegalArgumentException();List<String> key=List.of(tenant,event);if(delivered.contains(key))return "duplicate";Deque<Instant> q=windows.computeIfAbsent(tenant,t->new ArrayDeque<>());while(!q.isEmpty()&&!q.peek().plus(window).isAfter(now))q.remove();if(q.size()>=limit)return "rate-limited";q.add(now);sender.run();delivered.add(key);return "delivered";}
 }
 public static void main(String[] args){Notifications n=new Notifications(1,Duration.ofSeconds(10));Instant t=Instant.parse("2026-01-01T00:00:00Z");check(n.deliver("a","1",t,()->{}).equals("delivered"));check(n.deliver("a","1",t,()->{}).equals("duplicate"));check(n.deliver("a","2",t,()->{}).equals("rate-limited"));check(n.deliver("b","1",t,()->{}).equals("delivered"));check(n.deliver("a","2",t.plusSeconds(10),()->{}).equals("delivered"));System.out.println("PASS: rate window, tenant boundary and event deduplication");}
}
