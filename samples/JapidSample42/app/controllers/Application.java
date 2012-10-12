package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import models.Category;
import models.SearchParams;
import models.japidsample.Author;
import models.japidsample.Author2;
import models.japidsample.Post;
import play.data.Form;
import play.mvc.Http.Request;
import play.mvc.Result;
import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.template.RenderResult;
import cn.bran.play.JapidController;
import cn.bran.play.JapidResult;
/**
 *  A sample controller that demos Japid features
 *  
 * @author Bing Ran<bing_ran@hotmail.com>
 *
 */
public class Application extends JapidController {
	public static Result index() {
		return renderJapid(); // use the default index.html in the japidviews/SampleController directory
//		renderJapidWith("@index.html"); // use the default index.html in the japidviews/SampleController directory
	}
	public static Result indexAt() {
		return renderJapid(); // 
	}
	public static Result authorPanel(final Author a) {
//		boolean calledFromView = isInvokedfromJapidView();
//		System.out.println("calledFromView: " + calledFromView);
//		CacheableRunner r = new CacheableRunner("10s", genCacheKey()) {
//			@Override
//			protected RenderResult render() {
//				return new authorPanel().render(a);
//			}
//		};
//		
//		return new JapidResult(r.run());
		return null;
		//	or 		render(r);
	}
	
	public static Result authorPanel2(final Author a) {
		return renderJapid(a);
	}
	
//	public static Result cacheWithRenderJapid(final String a) {
////			CacheableRunner r = new CacheableRunner("5s", genCacheKey()) {
//		CacheableRunner r = new CacheableRunner("5s") {
//			@Override
//			protected RenderResult render() {
//				System.out.println("rerender...");
//				String b = a + new Date().getSeconds();
//				return getRenderResultWith("", b);
//			}
//		};
//		
////		throw new JapidResult(r.run()).eval(); // eval effectively cancel nested finer cache control
//		render(r);
//	}
//	
//	@CacheFor("6s")
	public static Result testCacheFor(String p) {
		System.out.println("rerender...");
		String b = "" + new Date().getSeconds();

		return renderJapid(b); // nested cache control still in effect
//		renderJapidEager(b); // nested cache control not in effect
	}
	
//	@CacheFor("3s")
	public static Result every3() {
		System.out.println("every3 called");
		String b = "" + new Date().getSeconds();
		return renderJapid(b); // nested cache control still in effect
	}
	
//	@CacheFor("5s")
	public static Result testCacheForEager(String p) {
		System.out.println("rerender...");
		String b = "" + new Date().getSeconds();
		return null;
//		renderJapidEager(b); // no nested cache control. the outer cache control overrides all
	}
	
	public static Result seconds() {
		String b = "" + new Date().getSeconds();
		return doHello(b);
	}
	
//	@CacheFor("4s")
	public static Result twoParams(String a, int b) {
		return renderText(a + "=" +  b + ":" + new Date().getSeconds());
	}
	
	
	public static Result foo() {
		StringBuilder sb = new StringBuilder();
		sb.append("--------------foo() action invoked:Hello foo!");
		RenderResult rr = new RenderResult(null, sb, 0);
		
		return  new JapidResult(rr);
		
//		runWithCache(new ActionRunner() {
//			@Override
//			public RenderResult run() {
//				return new authorPanel().render(a);
//			}
//		}, "10s", a);
	}
	
	public static Result hello() {
		String m = "hi there and....";
		String am = m + "!";
//		renderText("hello，Japid Play!");
		return doHello(am);
	}
	 static JapidResult doHello(String am) {
		return renderText(am);
	}
	
	
	public static Result h1() {
		System.out.println(">>" + Thread.currentThread());
		return renderJapid("h1");
	}
	/**
	 * this method shows how to render arguments to a japid template by naming and positional convention with the 
	 * renderJapid().
	 * 
	 */
	public static Result renderByPosition() {
		String s = "hello，renderByPosition！";
		int i = 1000;
		Author a = new Author();
		a.name = "author1";

		Author2 a2 = new Author2();
		a2.name = "author2";
		
		return renderJapid(s, i, a, a2, a2);
	}
	
	public static Result renderByPositionEmpty() {
		return renderJapid();
	}
	
	/**
	 * demo how to composite a page with independent segments with the #{invoke } tag
	 */
	public static Result composite() {
		Post post = new Post();
		post.title = "test post";
		post.postedAt = new Date();
		post.content = "this is perfect piece of content~!";
		
		Author a = new Author();
		a.name = "me";
		a.birthDate = new Date();
		a.gender = 'm';
		
		post.setAuthor(a);
		
		return renderJapid(post);
	}
	
	public static Result reverseLookup0() {
		return renderJapid();
	}

	public static Result reverseLookup1(String[] args) {
		return renderText("OK");
	}
	
	/**
	 * test the japid emailer
	 */
//	
//	public static Result email() {
//		Post p = new Post();
//		p.title = "我自己";
//		TestEmailer.emailme(p);
//		return renderText("mail sent");
//	}
//	
	public static Result callTag() {
		return renderJapidWith("templates/callPicka");
	}
	
	public static Result callTag2() {
		return renderJapidWith("templates/callPicka");
	}
	
	public static Result postList() {
		String title = "my Blog";
		List<Post> posts = createPosts();
		return renderJapidWith("templates/AllPost", title, posts);
	}
	/**
	 * @return
	 */
	private static List<Post> createPosts() {
		List<Post> posts = new ArrayList<Post>();
		Author a = new Author();
		a.name = "冉兵";
		a.birthDate = new Date();
		a.gender = 'M';
		Post p = new Post();
		p.author = a;
		p.content = "long time ago...";
		p.postedAt = new Date();
		p.title = "post 1";
		posts.add(p);
		p = new Post();
		p.author = a;
		p.content = "way too long time ago...";
		p.postedAt = new Date();
		p.title = "post 2";
		posts.add(p);
		return posts;
	}
	
	public static Result each() {
		List<String> list = Arrays.asList("as1", "as2", "as3", "as4", "as5", "as6");
		return renderJapidWith("templates/EachCall", list);
	}
	
	/**
	 * test using primitive with renderText
	 * @param i
	 */
	public static Result echo(Integer i) {
		System.out.println(">>>"+Thread.currentThread());
		
		Request req = request();
		return renderText( i * 8);
//		return renderText(req.uri() + i * 10);
//		return doecho(i);
	}

	public static Result ec(Integer i) {
//		Request req = request();
//		return renderText(i * 2);
		return doecho(i);
	}
	
	static Result doecho(int i) {
		Request req = request();
		return renderText(req.uri() + i * 2);
	}
	
	
	
	public static Result invokeInLoop() {
		return renderJapidWith("templates/invokeInLoop", createPosts());
	}
	
	public static Result echoPost(Post p) {
		return renderText(p);
	}
	
	/**
	 * "official" Play treats body as a special param name to store all POST body if the content type is 
	 * application/x-www-form-urlencoded. bran's fork has changed the reserved param name to _body. 
	 * 
	 * @param f1
	 * @param f2
	 * @param body
	 */
	public static Result dumpPost(String f1, String f2, String body) {
		if (f1 == null)
			f1 = "";
		
		if (f2 == null)
			f2 = "";
		
		if (body == null)
			body = "";
		else
			System.out.println("body: " + body);
		
		return renderJapidWith("templates/dumpPost.html", f1, f2, body);
	}

	/**
	 * POST method cannot pass args as parameters
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 * @return
	 */
	
	public static class Dump {
		public String f1, f2, body;
	}
	
	public static Result dumpPost2() {
		Form<Dump> dumpForm = form(Dump.class).bindFromRequest();
		
		Dump dump = dumpForm.get();
		
		String f1 = dump.f1;
		if (f1 == null)
			f1 = "";
		
		String f2 = dump.f2;
		if (f2 == null)
			f2 = "";
		
		String body = dump.body;
		if (body == null)
			body = "";
		else
			System.out.println("body: " + body);
		
		return renderJapidWith("templates/dumpPost.html", f1, f2, body);
	}
	
	public static Result in() {
		return out();
	}
	
	public static Result out() {
		return renderText("Hi out!");
	}

	public static Result go(String template) {
		return JapidController.renderJapidWith(template);
	}
	
	public static Result decorateName(String name) {
		return renderJapid(name);
	}
	public static Result verbatim() {
		return renderJapid();
	}
	
	public static Result ifs() {
		String s = "";
		List<String> list = new ArrayList<String>();
		list.add("a");
		Object[] array = list.toArray();
		return renderJapid(s, list, true, array, new int[] {}, 0, "a");
	}
	
	public static Result ifs2() {
		return renderJapid(2, new String[] {"as"});
	}
	
	public static Result flashbad() {
		flash().put("error", "something bad");
		// redirect to 
		return flashout();
	}

	public static Result flashMsg() {
		flash().put("msg", "a message");
		// redirect to 
		return flashout();
	}

	public static Result flashgood() {
		flash().put("success", "cool");
		// redirect to 
		return flashout();
	}
	
	public static Result flashout() {
		return renderJapid();
	}
	
	public static Result validate(String name, Integer age) {
//		   validation.required("name/姓名", name);
//		   validation.required("age/年龄", age);
//		   validation.min("age", age, 10);
		return renderJapidByName(named("name", name), named("age", age));
	}
	
	public static Result reverseUrl() {
		return renderJapid();
	}
	
	public static Result search(SearchParams sp) {
		return renderJapid(sp);
	}
	
	public static Result groovy() {
		String a = "Groovy";
		// render with Groovy
		return doHello(a);
	}
	
	public static Result list() {
		return renderJapid();
	}
	
	public static Result escapedExpr() {
		return renderJapid();
	}
	public static Result categories() {
		Category a = new Category();
		a.name = "a";
		Category cate1 = new Category();
		cate1.name = "1";
		Category cate2 = new Category();
		cate2.name = "2";
		Category cate11 = new Category();
		cate11.name = "11";
		Category cate12 = new Category();
		cate12.name = "12";
		a.subCategories = new ArrayList<Category>();
		a.subCategories.add(cate1);
		a.subCategories.add(cate2);
		cate1.subCategories = new ArrayList<Category>();
		cate1.subCategories.add(cate11);
		cate1.subCategories.add(cate12);
		
		List<Category> cats = new ArrayList<Category>();
		cats.add(a);
		return renderJapid(cats);
		
	}
	
	public static Result special() {
		return renderJapid();
	}
	
	public static Result hellohello() {
		return renderJapid();
	}

	public static Result js() {
		return renderJapid();
	}
	
	public static Result plainjapid() {
		return renderText(JapidRenderer.render("Steve Jobs"));
	}
	
}
