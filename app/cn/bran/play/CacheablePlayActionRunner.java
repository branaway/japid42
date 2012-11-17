package cn.bran.play;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import play.cache.Cached;
import cn.bran.japid.template.JapidRenderer;
import cn.bran.japid.template.RenderResult;

/**
 * The class defines a cached adapter to invoke a Play controller action. A new instance
 * must be used for each action invocation.
 * 
 * This class can be used in template, indirectly by Japid engine.
 * 
 * @author Bing Ran<bing_ran@hotmail.com>
 * 
 */
public abstract class CacheablePlayActionRunner extends CacheableRunner {
//	String controllerActionString;
//	private CacheFor cacheFor;
//	private boolean gotFromCacheForCache;
//	private String cacheForVal;
	private Class<? extends JapidController> controllerClass;
	private String actionName;
	
	/**
	 * 
	 * @param ttl
	 * @param args
	 * @deprecated use the other constructor that allow for better integration with CacheFor annotation
	 */
	public CacheablePlayActionRunner(String ttl, Object... args) {
		super(ttl, args);
//		if (args != null && args.length > 0) {
//			// the first argument is the methodInvocation string, like myController.myMethod
//			controllerActionString = (String) args[0];
//		}
	}
	
	public CacheablePlayActionRunner(String ttl, Class<? extends JapidController> controllerClass, String actionName, Object... args) {
		this.controllerClass = controllerClass;
		this.actionName = actionName;
		Object[] fullArgs = buildCacheKeyParts(controllerClass, actionName, args);
		super.init(ttl, fullArgs);
		
		//		Object[] fullArgs = new Object[args.length + 2];
//		System.arraycopy(args, 0, fullArgs, 0, args.length);
//		fullArgs[args.length] = controllerClass.getName();
//		fullArgs[args.length + 1] = actionName;
//		super.init(ttl, fullArgs);
	}
	

	public CacheablePlayActionRunner(String ttl) {
		super(ttl);
	}
	
	/**
	 * the action invocation to Play controller action, which must throw a
	 * JapidResult
	 * 
	 * @throws JapidResult
	 */
	protected abstract JapidResult runPlayAction();

	@Override
	protected RenderResult render() {
		JapidResult jr = runPlayAction();
		RenderResult rr = jr.getRenderResult();
		return rr;
	}

	/**
	 * @return
	 */
	protected boolean shouldCache() {
		fillCacheFor(controllerClass, actionName);
		return super.shouldCache();
	}

//	/**
//	 * @param jr
//	 */
//	@Override
//	protected void cacheResult(RenderResult jr) {
//		if (shouldCache()) {
//			// already cached
//		}
//		else {
//			super.cacheResult(jr);
//		}
//	}


//	/**
//	 * check the cache for the action. The cache should have been caused by the CacheFor annotation
//	 * 
//	 * @param class1
//	 * @param actionName
//	 * @deprecated the logic is not robust. new logic has been implemented in the super class.  
//	 * 
//	 */
//	protected void checkActionCacheFor(Class<? extends JapidController> class1, String actionName) {
//		fillCacheFor(class1, actionName);
//		
//		if (cacheForVal != null && cacheForVal.length() > 0) {
//			String key = super.keyString;
//			try {
////			Object v = play.cache.Cache.get(key);
//				 RenderResult v = RenderResultCache.get(key);
//				if (v != null) {
//					System.out.println("got from cache");
//					this.gotFromCacheForCache = true;
//					throw new JapidResult(v);
////					if (v instanceof JapidResult) {
////						throw ((JapidResult) v);
////					} else if (v instanceof CachedRenderResult) {
////						throw new JapidResult(((CachedRenderResult) v).rr);
////					} else {
//////								throw new RuntimeException("got something from the cache but not sure what it is: "
//////										+ v.getClass().getName());
////					}
//				}
//			} catch (ShouldRefreshException e) {
//			}
//			return;
//		}				
//	}

	/**
	 * @param class1
	 * @param actionName
	 */
	private void fillCacheFor(Class<? extends JapidController> class1, String actionName) {
		String className = class1.getName();
		String cacheForKey = className + "_" + actionName;
		Integer cacheForVal = (Integer) JapidRenderer.getCache().get(cacheForKey);
		if (cacheForVal == null) {
			// the cache has not been filled up yet.
			Method[] mths = class1.getDeclaredMethods();
			for (Method m : mths) {
				if (m.getName().equalsIgnoreCase(actionName) && Modifier.isPublic(m.getModifiers())) {
						Cached cacheFor = m.getAnnotation(Cached.class);
						if (cacheFor == null) {
							// well no annotation level cache spec
							cacheForVal = 0;
						}
						else {
							cacheForVal = cacheFor.duration();
						}
						JapidRenderer.getCache().put(cacheForKey, cacheForVal);
				}
			}
		}
		
		if (cacheForVal > 0){
			super.noCache = false;
			// only override ttlAbs if it's not specified.
			if (super.ttlAbs == null || super.ttlAbs.length() == 0)
				super.ttlAbs = cacheForVal + "s";
			
		}
	}

}
