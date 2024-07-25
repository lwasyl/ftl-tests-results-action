-dontobfuscate
-keepattributes SourceFile, LineNumberTable

-allowaccessmodification

-keep class io.github.lwasyl.ftl.cli.FtlTestsResultsCliKt {
  public static void main(java.lang.String[]);
}

# okio
-dontwarn org.codehaus.mojo.animal_sniffer.*

# Mordant rules
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations,AnnotationDefault
-dontwarn org.graalvm.**
-dontwarn com.oracle.svm.core.annotate.Delete
