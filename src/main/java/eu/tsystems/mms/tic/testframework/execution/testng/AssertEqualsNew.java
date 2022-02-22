package eu.tsystems.mms.tic.testframework.execution.testng;

import java.util.Set;

public abstract class AssertEqualsNew {

	protected static void assertNotEqualsNew(float actual1, float actual2, float delta, String message) {
		boolean fail;
		try {
		    Assert.assertEquals(actual1, actual2, delta, message);
		    fail = true;
		} catch (AssertionError e) {
		    fail = false;
		}
	
		if (fail) {
		    Assert.fail(message);
		}
	}

	protected static void assertNotEqualsNew2(Set<?> actual, Set<?> expected, String message) {
		boolean fail;
	    try {
	        Assert.assertEquals(actual, expected, message);
	        fail = true;
	    } catch (AssertionError e) {
	        fail = false;
	    }
	
	    if (fail) {
	        Assert.fail(message);
	    }
	}

	public static void assertNotEquals(Object actual1, Object actual2, String message) {
	    boolean fail;
	    try {
	        Assert.assertEquals(actual1, actual2);
	        fail = true;
	    } catch (AssertionError e) {
	        fail = false;
	    }
	
	    if (fail) {
	        Assert.fail(message);
	    }
	}


	public static void assertNotEquals(double actual1, double actual2, double delta, String message) {
	    boolean fail;
	    try {
	        Assert.assertEquals(actual1, actual2, delta, message);
	        fail = true;
	    } catch (AssertionError e) {
	        fail = false;
	    }
	
	    if (fail) {
	        Assert.fail(message);
	    }
	}

}
