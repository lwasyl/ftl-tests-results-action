-dontobfuscate
-keepattributes SourceFile, LineNumberTable

-allowaccessmodification

-keep class org.usefulness.ftl.cli.FtlTestsResultsCliKt {
  public static void main(java.lang.String[]);
}

# okio
-dontwarn org.codehaus.mojo.animal_sniffer.*
