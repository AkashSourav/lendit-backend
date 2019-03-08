package com.codimen.lendit.security;

public class ContextStorage {
	static private ThreadLocal<ContextData> threadLocal = new ThreadLocal<>();

	public static void set(ContextData cd) {
		threadLocal.set(cd);
	}

	public static void unset() {
		threadLocal.remove();
	}

	public static ContextData get() {
		return threadLocal.get();
	}
}