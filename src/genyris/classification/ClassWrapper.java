package genyris.classification;

import org.lispin.jlispin.classes.BuiltinClasses;
import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Constants;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.ExpWithEmbeddedClasses;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.Visitor;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.UnboundException;

public class ClassWrapper extends ExpWithEmbeddedClasses {
    private Lobject _theClass;
    private Exp     CLASSNAME, SUPERCLASSES;
    private Lsymbol NIL;

    public ClassWrapper(Lobject toWrap) {
        _theClass = toWrap;
        CLASSNAME = toWrap.getParent().internString(Constants.CLASSNAME);
        SUPERCLASSES = toWrap.getParent().internString(Constants.SUPERCLASSES);
        NIL = toWrap.getParent().getNil();
    }

    public void acceptVisitor(Visitor guest) {
        guest.visitClassWrapper(this);
    }

    public Object getJavaValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public String toString() {
        String result = "<class ";
        try {
            result += getClassName();
            Exp classes = getSuperClasses();
            result += " (";
            while (classes != NIL) {
                ClassWrapper klass = new ClassWrapper((Lobject)classes.car());
                result += klass.getClassName();
                classes = classes.cdr();
            }
            result += ")";
            result += ">";
        } catch (UnboundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private Exp getSuperClasses() {
        try {
            return _theClass.lookupVariableShallow(SUPERCLASSES);
        } catch (UnboundException e) {
            return NIL;
        }
    }

    private String getClassName() throws UnboundException {
        return _theClass.lookupVariableShallow(CLASSNAME).toString();
    }

    public static Lobject makeClass(Environment env, Exp klassname, Exp superklasses) throws LispinException {
        Exp NIL = env.getNil();
        Lobject newClass = new Lobject(env);
        newClass.addClass(BuiltinClasses.STANDARDCLASS);
        newClass.defineVariable(env.internString(Constants.CLASSNAME), klassname);
        newClass.defineVariable(env.internString(Constants.CLASSES),
                new Lcons(BuiltinClasses.STANDARDCLASS, NIL));
        newClass.defineVariable(env.internString(Constants.SUBCLASSES), NIL);
        if (superklasses != NIL) {
            if (superklasses != NIL) {
                newClass.defineVariable(env.internString(Constants.SUPERCLASSES),
                        lookupClasses(env, superklasses));
                Exp sklist = superklasses;
                while (sklist != NIL) {
                    Lobject sk = (Lobject)(env.lookupVariableValue(sklist.car()));
                    Exp subklasses = NIL;
                    try {
                        subklasses = sk.lookupVariableShallow(env.internString(Constants.SUBCLASSES));
                    } catch (UnboundException ignore) {
                        sk.defineVariable(env.internString(Constants.SUBCLASSES), NIL);
                    }
                    sk.setVariableValue(env.internString(Constants.SUBCLASSES), new Lcons(newClass,
                            subklasses));
                    sklist = sklist.cdr();
                }
            }
        }
        env.defineVariable(klassname, newClass);
        return newClass;
    }
    private static Exp lookupClasses(Environment env, Exp superklasses) throws LispinException {
        Exp result = env.getNil();
        while(superklasses != env.getNil()) {
            result = new Lcons(env.lookupVariableValue(superklasses.car()), result);
            superklasses = superklasses.cdr();
        }
        return result;
    }
    }
