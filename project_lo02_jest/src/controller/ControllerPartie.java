package controller;

/**
 * Contrôleur de lancement pour l'ancienne version en mode console uniquement.
 * Cette classe est obsolète - utiliser JestGame à la place.
 * 
 * @deprecated Utiliser JestGame pour le nouveau système MVC avec support GUI
 */
@Deprecated
public class ControllerPartie {

	/**
	 * Constructeur vide.
	 */
	public ControllerPartie() {
	}

	/**
	 * Lance une nouvelle partie de Jest en mode console. Crée une instance de
	 * PartieJest et démarre le jeu.
	 */
	public static void run() {
		System.out.println("==== LANCEMENT DU JEU JEST ====");

		// Création d'une nouvelle partie
		PartieJest partie = new PartieJest();

	}

	/**
	 * Point d'entrée principal pour l'ancienne version console.
	 * 
	 * @param args arguments de ligne de commande (non utilisés)
	 */
	public static void main(String[] args) {
		run();
	}

}