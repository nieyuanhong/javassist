import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class Jassist150 {

    public static final String BASE_PATH="./";
    public static final String JAVASSIST_JAR=BASE_PATH+"javassist.jar";
    public static final String CLASSES_FOLDER=BASE_PATH+"build/classes";
    public static final String TEST_CLASSES_FOLDER=BASE_PATH+"build/test-classes";

    public static class Inner1 {
      public static int get() {
        return 0;
      }
    }

    public static void implTestClassTailCache() throws NotFoundException, CannotCompileException {
      ClassPool pool = new ClassPool(true);
      for(int paths=0; paths<50; paths++) {
        pool.appendClassPath(JAVASSIST_JAR);
        pool.appendClassPath(CLASSES_FOLDER);
        pool.appendClassPath(TEST_CLASSES_FOLDER);
      }
      CtClass cc = pool.get("Jassist150$Inner1");
      CtMethod ccGet = cc.getDeclaredMethod("get");
      String code1 = "{ int n1 = Integer.valueOf(1); " +
          "  int n2 = Integer.valueOf(2); " +
          "  int n3 = Integer.valueOf(3); " +
          "  int n4 = Integer.valueOf(4); " +
          "  int n5 = Integer.valueOf(5); " +
          "  return n1+n2+n3+n4+n5; }";
      String code2 = "{ int n1 = java.lang.Integer.valueOf(1); " +
          "  int n2 = java.lang.Integer.valueOf(2); " +
          "  int n3 = java.lang.Integer.valueOf(3); " +
          "  int n4 = java.lang.Integer.valueOf(4); " +
          "  int n5 = java.lang.Integer.valueOf(5); " +
          "  return n1+n2+n3+n4+n5; }";
      String code3 = "{ int n1 = java.lang.Integer#valueOf(1); " +
          "  int n2 = java.lang.Integer#valueOf(2); " +
          "  int n3 = java.lang.Integer#valueOf(3); " +
          "  int n4 = java.lang.Integer#valueOf(4); " +
          "  int n5 = java.lang.Integer#valueOf(5); " +
          "  return n1+n2+n3+n4+n5; }";
      loop(cc, ccGet, code1);
    }

    public static void loop(CtClass cc, CtMethod ccGet, String code) throws CannotCompileException {
        long startTime = System.currentTimeMillis();
        for(int replace=0; replace<1000; replace++) {
            ccGet.setBody(code);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Test: Time (ms) "+(endTime-startTime));
    }

    public static void main(String[] args) {
    for (int loop = 0; loop < 5; loop++) {
        try {
                implTestClassTailCache();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
  }
}