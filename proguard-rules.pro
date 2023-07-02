-dontobfuscate
-keepattributes SourceFile, LineNumberTable

-allowaccessmodification

-keep class io.github.lwasyl.ftl.cli.FtlTestsResultsCliKt {
  public static void main(java.lang.String[]);
}

# okio
-dontwarn org.codehaus.mojo.animal_sniffer.*
