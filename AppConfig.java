package eneseminozbilgin;

public class AppConfig {
	private static boolean GuiMode = false;

    public static void setGuiMode(boolean mode) {
        GuiMode = mode;
    }

    public static boolean getGuiMode() {
        return GuiMode;
    }
}
