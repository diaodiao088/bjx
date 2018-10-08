package com.bjxapp.worker.http.keyboard.commonutils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class ReflectUtil {

	private static boolean isEmpty(String string) {
		if (string == null || string.length() == 0)
			return true;
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T invoke(String method, Object receiver, Class<T> returnClass,
			Class[] paramsClass, Object[] params) {
		if (isEmpty(method) || receiver == null) {
			throw new NullPointerException("reflect method or receiver is null");
		}
		
		if (paramsClass == null && params != null)
			throw new IllegalArgumentException("illegal agument");
		
		if (paramsClass != null && params != null
				&& params.length != paramsClass.length)
			throw new IllegalArgumentException("illegal aguments count");
		try {
			Method methodObj = receiver.getClass().getDeclaredMethod(method, paramsClass);
			methodObj.setAccessible(true);
			return (T)methodObj.invoke(receiver, params);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static final Class<?> getRawType(Type type) {
		Preconditions.checkNotNull(type, "type == null");

		if (type instanceof Class<?>) {
			// Type is a normal class.
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;

			// I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
			// suspects some pathological case related to nested classes exists.
			Type rawType = parameterizedType.getRawType();
			if (!(rawType instanceof Class)) throw new IllegalArgumentException();
			return (Class<?>) rawType;
		}
		if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			return Array.newInstance(getRawType(componentType), 0).getClass();
		}
		if (type instanceof TypeVariable) {
			// We could use the variable's bounds, but that won't work if there are multiple. Having a raw
			// type that's more general than necessary is okay.
			return Object.class;
		}
		if (type instanceof WildcardType) {
			return getRawType(((WildcardType) type).getUpperBounds()[0]);
		}

		throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
				+ "GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
	}

	public final static Type getParameterUpperBound(int index, ParameterizedType type) {
		Type[] types = type.getActualTypeArguments();
		if (index < 0 || index >= types.length) {
			throw new IllegalArgumentException(
					"Index " + index + " not in range [0," + types.length + ") for " + type);
		}
		Type paramType = types[index];
		if (paramType instanceof WildcardType) {
			return ((WildcardType) paramType).getUpperBounds()[0];
		}
		return paramType;
	}

	public final static void fieldSet(Object object, String fieldName, Object value) throws Exception {
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(object, value);
	}

	public static Object fieldGet(Object object, String fieldName) throws Exception {
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(object);
	}
}
