package Core;
import java.util.Locale;

import javafx.application.Application;

public class Main {
	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		Application.launch(SkyView.class, args);
	}
}
