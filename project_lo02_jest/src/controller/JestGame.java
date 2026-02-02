package controller;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import view.ConsoleView;
import view.gui.MainMenuFrame;

/**
 * Point d'entrée principal de l'application JEST. Permet de lancer le jeu en
 * mode Console, GUI ou les deux simultanément.
 */
public class JestGame {

	/**
	 * Point d'entrée principal de l'application JEST. Crée un contrôleur de jeu et
	 * lance le jeu en mode double (Console + GUI).
	 * 
	 * @param args arguments de ligne de commande (non utilisés)
	 */
	public static void main(String[] args) {
		System.out.println("==============================================");
		System.out.println("          JEST - The Card Game");
		System.out.println("==============================================");
		System.out.println();
		System.out.println();

		GameController controller = new GameController();
		launchBoth(controller);
	}

	/**
	 * Lance uniquement l'interface graphique.
	 * 
	 * @param controller le contrôleur de jeu à utiliser
	 */
	private static void launchGUI(GameController controller) {

		// Définir le look and feel du système
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> {
			MainMenuFrame menu = new MainMenuFrame(controller);
			menu.setVisible(true);
		});
	}

	/**
	 * Lance uniquement la vue console.
	 * 
	 * @param controller le contrôleur de jeu à utiliser
	 */
	private static void launchConsole(GameController controller) {
		ConsoleView consoleView = new ConsoleView(controller);
		consoleView.start();
	}

	/**
	 * Lance les deux interfaces simultanément (GUI et Console). Les deux vues
	 * observent le même contrôleur et se synchronisent automatiquement.
	 * 
	 * @param controller le contrôleur de jeu à utiliser
	 */
	private static void launchBoth(GameController controller) {
		System.out.println("📝 Lancement du jeu");
		System.out.println("🎮 Veuillez paramétrer le jeu dans l'interface graphique");
		System.out.println();

		// Définir le look and feel du système
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Lancer la GUI (qui créera la ConsoleView)
		SwingUtilities.invokeLater(() -> {
			MainMenuFrame menu = new MainMenuFrame(controller);
			menu.setVisible(true);
		});

	}
}
