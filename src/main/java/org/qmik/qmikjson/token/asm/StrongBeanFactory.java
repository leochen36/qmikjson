package org.qmik.qmikjson.token.asm;
/**
 * 增强bean类
 * @author leo
 *
 */
public class StrongBeanFactory {
	
	private final static StrongBeanClump	clump	= new StrongBeanClump();
	private final static StrongBean			bean	= new StrongBean();
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<?> superClazz, Class<?>... superInterfaces) {
		T value = null;
		String key = superClazz.getName() + StrongBean.suffix;
		Class<?> strongClass;
		
		if (clump.get(key) != null) {
			strongClass = clump.get(key);
		} else {
			synchronized (clump) {
				if (clump.get(key) == null) {
					strongClass = bean.makeClass(superClazz, superInterfaces);
					clump.add(key, strongClass);
				}
			}
		}
		try {
			value = (T) clump.get(key).newInstance();
		} catch (Exception e) {
			throw new NewInstanceException(e);
		}
		return value;
	}
}
