package app.process.command;

public final class CommandFactory {

    public static ICommand gitCheckout(final String branch){
        return () -> new String[]{"git", "checkout", branch};
    }

    public static ICommand gitPull(){
        return () -> new String[]{"git", "pull"};
    }

    public static ICommand gradleShadowJar(){
        return () -> new String[]{"gradle", "shadowJar"};
    }

    public static ICommand copy(final String source, final String target){
        return () -> new String[]{"cp", source, target};
    }

    public static ICommand remove(final String target, final boolean recursive){
        return recursive? () -> new String[]{"rm", "-r", target} : () -> new String[]{"rm", target};
    }

    public static ICommand runJar(final String jarPath){
        return () -> new String[]{"java", "-jar",
                "-Xms512m", "-Xmx12G" ,"-XX:+UnlockExperimentalVMOptions",
                "-XX:+UseG1GC" ,"-XX:G1NewSizePercent=20",
                "-XX:G1ReservePercent=20", "-XX:MaxGCPauseMillis=6500" ,
                "-XX:G1HeapRegionSize=32M", jarPath};
    }

}
