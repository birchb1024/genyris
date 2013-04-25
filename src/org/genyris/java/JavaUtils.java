package org.genyris.java;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.ReaderInStream;
import org.genyris.io.readerstream.ReaderStream;

public class JavaUtils {
    public static String toGenyrisName(String javaClassName) {
        return javaClassName.replace('.', '_').replace('[', '*');
    }

    public static Exp javaToGenyris(Environment env, Object rawResult) {
        Exp NIL = env.getNil();
        if ((rawResult == null) || (rawResult instanceof Void)) {
            return NIL;
        } else if (rawResult instanceof Boolean) {
            return ((Boolean) rawResult).booleanValue() ? env.getSymbolTable().TRUE()
                    : NIL;
        } else if (rawResult instanceof Character) {
            return new StrinG(((Character) rawResult).toString());
        } else if (rawResult instanceof String) {
            return new StrinG((String) rawResult);
        } else if (rawResult instanceof Integer) { // TODO move this into a
            // factory in Bignum?
            return new Bignum((Integer) rawResult);
        } else if (rawResult instanceof Long) {
            return new Bignum((Long) rawResult);
        } else if (rawResult instanceof Double) {
            return new Bignum((Double) rawResult);
        } else if (rawResult instanceof Float) {
            return new Bignum((Float) rawResult);
        } else if (rawResult instanceof Short) {
            return new Bignum((Short) rawResult);
        } else if (rawResult instanceof Byte) {
            return new Bignum((Byte) rawResult);
        } else if (rawResult instanceof BigDecimal) {
            return new Bignum((BigDecimal) rawResult);
        } else if (rawResult.getClass().isArray()) {
            Exp retval = NIL;
            int last = Array.getLength(rawResult) - 1;
            for (int i = last; i >= 0; i--) {
                retval = new Pair(javaToGenyris(env, Array.get(rawResult, i)), retval);
            }
            return retval;
        } else if (rawResult instanceof java.io.Reader) {
            return new ReaderStream(new ReaderInStream((java.io.Reader) rawResult,
                    ((java.io.Reader) rawResult).toString()));
        }
        JavaWrapper result = wrapJavaObject(env, rawResult);
        return result;
    }

    public static JavaWrapper wrapJavaObject(Environment env, Object javaObject) {
        JavaWrapper result = new JavaWrapper(javaObject);
        try {
            Class resultClass = javaObject.getClass();
            Exp klass = env.lookupVariableValue(env
                    .internString(toGenyrisName(resultClass.getName())));
            result.addClass((Dictionary) klass);
        } catch (UnboundException e) {
            ;
        }
        return result;
    }

    public static Object[] toJavaArray(Class[] params, Exp[] arguments, Environment env)
            throws GenyrisException {
        if (params.length == 0)
            return new Object[0];
        Object[] result = new Object[params.length];
        if (params.length != arguments.length) {
            throw new GenyrisException("toJavaArray: missmatched lengths!");
        }
        for (int i = 0; i < params.length; i++) {
            result[i] = convertToJava(params[i], arguments[i], env);
        }
        return result;
    }

    public static Object convertToJava(Class klass, Exp exp, Environment env)
            throws GenyrisException {
        if (exp instanceof JavaWrapper) {
            return ((JavaWrapper) exp).getValue();
        } else if (klass == exp.getClass()) {
            return exp;
        } else if (klass.isInstance(exp)) {
            return exp;
        } else if (klass == java.lang.Void.TYPE) {
            throw new UnboundException("convertToJava: java.lang.Void.");
        } else if (klass == java.lang.Boolean.TYPE || klass == java.lang.Boolean.class) {
            return Boolean.valueOf(exp == env.getNil() ? false : true);
        } else if (exp instanceof Bignum) {
            BigDecimal big = ((Bignum) exp).bigDecimalValue();
            if (klass == java.lang.Byte.TYPE || klass == java.lang.Byte.class) {
                return new java.lang.Byte(big.byteValue());
            } else if (klass == java.lang.Short.TYPE || klass == java.lang.Short.class) {
                return new java.lang.Short(big.shortValue());
            } else if (klass == java.lang.Integer.TYPE
                    || klass == java.lang.Integer.class) {
                return new java.lang.Integer(big.intValue());
            } else if (klass == java.lang.Long.TYPE || klass == java.lang.Long.class) {
                return new java.lang.Long(big.longValue());
            } else if (klass == java.lang.Float.TYPE || klass == java.lang.Float.class) {
                return new java.lang.Float(big.floatValue());
            } else if (klass == java.lang.Double.TYPE || klass == java.lang.Double.class) {
                return new java.lang.Double(big.doubleValue());
            } else if (klass == java.lang.String.class) {
                return big.toString();
            } else if (klass == java.lang.Object.class) {
                return (Object) big;
            } else if (klass == java.math.BigDecimal.class) {
                return big;
            }
        } else if (klass == java.io.Reader.class && ReaderStream.class.isInstance(exp)) {
            return ((ReaderStream) exp).getReader();
        } else if (StrinG.class.isInstance(exp) || Symbol.class.isInstance(exp)) {
            if (klass == java.lang.Character.TYPE || klass == java.lang.Character.class) {
                return new java.lang.Character(exp.toString().charAt(0));
            } else
                return exp.toString();
        } else if (klass == (new byte[1]).getClass()) {
            // Byte array
            if (!(exp instanceof Pair)) {
                throw new UnboundException("convertToJava: was expecting a list.");
            }
            int length = exp.length(env.getNil());
            byte[] result = new byte[length];
            for (int i = 0; i < length; i++) {
                Exp item = exp.car();
                if (!(item instanceof Bignum)) {
                    throw new GenyrisException("Non-bignum in byte aray conversion: "
                            + item);
                }
                result[i] = (byte) ((Bignum) item).bigDecimalValue().intValue();
                exp = exp.cdr();
            }
            return result;

        } else if (klass.isArray()) {
            if (!(exp instanceof Pair)) {
                throw new UnboundException("convertToJava: was expecting a list.");
            }
            int length = exp.length(env.getNil());
            Class elementType = klass.getComponentType();
            Object[] result = (Object[]) Array.newInstance(elementType, length);
            for (int i = 0; i < length; i++) {
                result[i] = convertToJava(elementType, exp.car(), env);
                exp = exp.cdr();
            }
            return result;

        } else {
            throw new UnboundException("unsupported conversion: " + klass + " " + exp);
        }
        throw new UnboundException("unsupported conversion: " + klass + " " + exp);
    }

    public static Exp importJavaClass(Interpreter interp, String genyrisClassName,
            Environment env, String javaClassName) throws GenyrisException {
        Class klass = null;
        try {
            klass = Class.forName(javaClassName);
        } catch (ClassNotFoundException e) {
            throw new GenyrisException("Java ClassNotFoundException: " + e.getMessage());
        }
        JavaClass genyrisClass = (JavaClass) JavaClass.makeClass(klass,
                interp.getGlobalEnv(), interp.intern(toGenyrisName(javaClassName)),
                new Pair(interp.intern(Constants.JAVA), interp.NIL));
        genyrisClass.addProperty(env, "java-classname", new StrinG(javaClassName));
        if (!genyrisClassName.equals(javaClassName)) {
            env.defineVariable(interp.intern(genyrisClassName), genyrisClass);
        }
        //
        Constructor[] ctors = klass.getConstructors();
        for (int c = 0; c < ctors.length; c++) {
            Class[] params = ctors[c].getParameterTypes();
            String name = "new";
            name += argList(params);
            JavaCtor gctor = new JavaCtor(interp, genyrisClass, name, ctors[c], params);
            genyrisClass.defineDynamicVariable(new DynamicSymbol(interp.intern(name)),
                    new EagerProcedure(env, interp.NIL, gctor));
        }
        //
        Method[] methods = klass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Class[] params = methods[i].getParameterTypes();
            String name = methods[i].getName() + argList(params);
            ApplicableFunction gmethod;
            if (Modifier.isStatic(methods[i].getModifiers())) {
                gmethod = new JavaStaticMethod(interp, name, methods[i], params);
            } else {
                gmethod = new JavaMethod(interp, name, methods[i], params);
            }
            SimpleSymbol sym = interp.intern(name);
            genyrisClass.defineDynamicVariable(new DynamicSymbol(sym),
                    new EagerProcedure(env, interp.NIL, gmethod));

        }
        return genyrisClass;
    }

    private static StringBuffer argList(Class[] params) {
        StringBuffer name = new StringBuffer();
        if (params.length > 0) {
            for (int p = 0; p < params.length; p++) {
                name.append('-');
                name.append(toGenyrisName(params[p].getName()));
            }
        }
        return name;
    }

}
