package app.process.command;

public final class CommandFactory {

    public static ICommand gitCheckout(final String branch){
        return () -> new String[]{"git", "checkout", branch};
    }

    public static ICommand gradleShadowJar(){
        return () -> new String[]{"gradle", "shadowJar"};
    }

    public static ICommand copy(final String source, final String target){
        return () -> new String[]{"cp", source, target};
    }

    public static ICommand runJar(final String jarPath){
        return () -> new String[]{"java", "-jar", jarPath};
    }

}
