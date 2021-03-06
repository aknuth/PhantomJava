package examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.phantomjscef.websocket.PhantomJs;

public class TwitterParallel {

	public static void main(String[] args) {
		PhantomJs.startServer();
		if (System.getProperty("phantomjsDir") != null){
			String phantomjsDir = System.getProperty("phantomjsDir");
			PhantomJs.startPhantomProcess(phantomjsDir);
		}
		
		String[] users = {"PhantomJS","ariyahidayat","Vitalliumm"};
		
		List<CompletableFuture<String>> cfList = new ArrayList<CompletableFuture<String>>();
		
		for (String user: users){
			CompletableFuture<String> finish = new CompletableFuture<String>();
			follow(user,finish);
			cfList.add(finish);
		}
		
		CompletableFuture<String>[] cfArray = cfList.stream().toArray(size -> new CompletableFuture[size]);
		CompletableFuture.allOf(cfArray).handle((ok,ex) -> {
			PhantomJs.exit();
			return null;
		});
	}
	
	private static void follow(String user, CompletableFuture<String> finish){
		PhantomJs.waitForAckFuture().thenCompose((ev) -> {
			return PhantomJs.loadUrl("http://mobile.twitter.com/"+user);
		}).thenCompose((page) -> {
			return page.text(".UserProfileHeader-statCount");
		}).thenApply((pText) -> {
			System.out.println(">>>>>>>>> "+user+" folgt "+pText.getText()+" Personen");
			return finish.complete("READY");
		});
	}
}
