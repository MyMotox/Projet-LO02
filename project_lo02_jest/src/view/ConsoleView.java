package view;

import java.util.List;
import java.util.Scanner;

import controller.GameController;
import model.Card;
import model.InteractivePlayerStrategy;
import model.Offer;
import model.Player;

/**
 * Implémentation de l'interface utilisateur en mode ligne de commande.
 * <p>
 * Affiche les événements du jeu sur la sortie standard et récupère les entrées
 * utilisateur via un {@link java.util.Scanner}.
 * </p>
 * 
 * @author Mathéo A.
 * @author Jules CS
 * @version 1.0
 */
public class ConsoleView implements GameViewInterface {

	private GameController controller;
	private InteractivePlayerStrategy playerStrategy;
	private Scanner scanner;
	private boolean active = true;

	/**
	 * Construit une vue console attachée à un contrôleur. S'enregistre
	 * automatiquement comme observateur du contrôleur.
	 * 
	 * @param controller le contrôleur de jeu à observer
	 */
	public ConsoleView(GameController controller) {
		this.controller = controller;
		this.scanner = new Scanner(System.in);
		this.playerStrategy = new InteractivePlayerStrategy(this);
		this.controller.addObserver(this);
	}

	/**
	 * Retourne la stratégie de joueur interactif utilisée par cette vue.
	 * 
	 * @return la stratégie de joueur interactif
	 */
	public InteractivePlayerStrategy getPlayerStrategy() {
		return playerStrategy;
	}

	/**
	 * Démarre la vue console. Affiche le titre et le menu principal.
	 */
	@Override
	public void start() {
		displayTitle();
		showMainMenu();
	}

	/**
	 * Ferme la vue console et libère les ressources.
	 */
	@Override
	public void close() {
		active = false;
		scanner.close();
	}

	/**
	 * Affiche le titre ASCII du jeu.
	 */
	private void displayTitle() {
		System.out.println("\n" + "█████████████████████████\n" + "███▄─▄█▄─▄▄─█─▄▄▄▄█─▄─▄─█\n"
				+ "█─▄█─███─▄█▀█▄▄▄▄─███─███\n" + "▀▄▄▄▀▀▀▄▄▄▄▄▀▄▄▄▄▄▀▀▄▄▄▀▀\n");
	}

	/**
	 * Affiche le menu principal et gère le choix de l'utilisateur. Propose de
	 * démarrer une nouvelle partie ou de charger une sauvegarde.
	 */
	private void showMainMenu() {
		System.out.println("♻️ Voulez-vous : (1) Nouvelle partie | (2) Charger une partie");
		int choice = scanner.nextInt();

		if (choice == 2) {
			loadGame();
		} else {
			configureNewGame();
		}
	}

	/**
	 * Charge une partie sauvegardée. Affiche la liste des sauvegardes et permet à
	 * l'utilisateur d'en choisir une.
	 */
	private void loadGame() {
		List<String> saves = controller.getAvailableSaves();

		if (saves.isEmpty()) {
			System.out.println("📝 Aucune sauvegarde trouvée. Démarrage d'une nouvelle partie...");
			configureNewGame();
			return;
		}

		System.out.println("♻️ Sauvegardes disponibles :");
		for (int i = 0; i < saves.size(); i++) {
			System.out.println((i + 1) + ". " + saves.get(i));
		}
		System.out.println("📝 Choisissez une sauvegarde (numéro) :");
		int choice = scanner.nextInt() - 1;

		if (choice < 0 || choice >= saves.size()) {
			System.out.println("❌ Choix invalide. Démarrage d'une nouvelle partie...");
			configureNewGame();
			return;
		}

		controller.loadGame(saves.get(choice));
		controller.runGameLoop();
	}

	/**
	 * Configure et démarre une nouvelle partie. Demande les paramètres de jeu :
	 * extension, nombre de joueurs, noms, mode.
	 */
	private void configureNewGame() {
		// Extension
		System.out.println("🃏 Souhaitez-vous activer l'extension Carte Bonus/Malus/Gold ? (0) Non | (1) Oui ");
		int choixExt = scanner.nextInt();
		boolean extensionActive = (choixExt == 1);

		if (extensionActive) {
			System.out.println("✅ Extension Carte Bonus/Malus/Gold activée !");
		} else {
			System.out.println("❌ Extension désactivée.");
		}

		// Joueurs humains
		System.out.println("🧑‍🧑‍🧒‍🧒 Choisissez le nombre de joueurs Humain (max 4) :");
		int nbHumanPlayers = Math.max(0, Math.min(4, scanner.nextInt()));

		// Bots
		int nbBotPlayers = 0;
		java.util.List<Boolean> botTypes = new java.util.ArrayList<>();

		if (nbHumanPlayers < 4) {
			System.out.println("🤖 Choisissez le nombre de robot (max " + (4 - nbHumanPlayers) + ")");
			nbBotPlayers = Math.max(0, Math.min(4 - nbHumanPlayers, scanner.nextInt()));

			if (nbHumanPlayers + nbBotPlayers < 2) {
				nbBotPlayers = 2 - nbHumanPlayers;
			}

			for (int i = 0; i < nbBotPlayers; i++) {
				System.out.println("🤖 Souhaitez-vous rajouter pour le robot " + (i + 1)
						+ " une stratégie aléatoire ou un robot tricheur? (1) Robot Aléatoire | (2) Robot Tricheur");
				int choice = scanner.nextInt();
				botTypes.add(choice == 2);

				if (choice == 2) {
					System.out.println("🛸 HAL-9000-" + (i + 1) + " le robot manipulateur a été rajouté avec succès !");
				} else {
					System.out.println("🤪 Bender-" + (i + 1) + " le robot imprévisible a été rajouté avec succès !");
				}
			}
		}

		// Mode de jeu
		System.out.println("🕹 ️Choisissez votre mode de jeu :");
		System.out.println("(1) ☕️ JEST classique - Règles classiques de JEST");
		System.out.println(
				"(2) 👁️ JEST Bouffon - Le joueur avec la moins bonne main devient bouffon et commence le round");
		System.out.println("(3) 🎭 JEST Clair - Le stack est affiché avant la distribution des cartes");
		int choixMode = scanner.nextInt();
		int mode = 0;

		if (choixMode == 2) {
			mode = 1;
			System.out.println("🎭 Vous avez choisi le mode JEST Bouffon !");
		} else if (choixMode == 3) {
			mode = 2;
			System.out.println("👁 Vous avez choisi le mode JEST Clair !");
		}

		// Noms des joueurs
		java.util.List<String> playerNames = new java.util.ArrayList<>();
		for (int i = 0; i < nbHumanPlayers; i++) {
			System.out.println("📝 Entrez le nom du joueur " + (i + 1) + " :");
			String name = scanner.next();
			playerNames.add(name);
		}

		// Démarrer la partie
		controller.setHumanStrategy(playerStrategy);
		controller.startNewGame(nbHumanPlayers, nbBotPlayers, playerNames, botTypes, mode, extensionActive);
		controller.runGameLoop();
	}

	// === MÉTHODES D'INTERACTION ===

	@Override
	public int askForOffer(Player player) {
		System.out.println("\n>>> " + player.getName() + " - Quelle carte CACHER ?");
		System.out.println("(1) Cacher " + player.getHand().get(0));
		System.out.println("(2) Cacher " + player.getHand().get(1));
		System.out.print(">>> Entrez 1 ou 2 : ");
		System.out.flush();

		int choice;
		synchronized (scanner) {
			String input = scanner.nextLine().trim();
			choice = Integer.parseInt(input);
		}
		System.out.println("✓ Choix enregistré : " + choice);

		// Envoyer la réponse à la stratégie
		if (playerStrategy != null) {
			playerStrategy.provideResponse(choice);
		}

		return choice;
	}

	@Override
	public int askForTarget(Player player, List<Offer> availableOffers) {
		System.out.println("\n>>> " + player.getName() + " - Choisissez une cible:");
		for (int i = 0; i < availableOffers.size(); i++) {
			Offer o = availableOffers.get(i);
			System.out.println("(" + (i + 1) + ") " + o.getOwner().getName());
		}
		System.out.print(">>> Entrez le numéro : ");
		System.out.flush();

		int choice;
		synchronized (scanner) {
			String input = scanner.nextLine().trim();
			choice = Integer.parseInt(input);
		}
		System.out.println("✓ Choix enregistré : " + choice);

		// Envoyer la réponse à la stratégie
		if (playerStrategy != null) {
			playerStrategy.provideResponse(choice);
		}

		return choice;
	}

	@Override
	public int askForCard(Player player, Offer targetOffer) {
		System.out.println("\n>>> " + player.getName() + " - Quelle carte prendre ?");
		if (targetOffer.getVisibleCard() != null) {
			System.out.println("(1) Carte visible: " + targetOffer.getVisibleCard());
		}
		if (targetOffer.getHiddenCard() != null) {
			System.out.println("(2) Carte cachée: ???");
		}
		System.out.print(">>> Entrez 1 ou 2 : ");
		System.out.flush();

		int choice;
		synchronized (scanner) {
			String input = scanner.nextLine().trim();
			choice = Integer.parseInt(input);
		}
		System.out.println("✓ Choix enregistré : " + choice);

		// Envoyer la réponse à la stratégie
		if (playerStrategy != null) {
			playerStrategy.provideResponse(choice);
		}

		return choice;
	}

	@Override
	public void displayMessage(String message) {
		System.out.println(message);
	}

	// === OBSERVATEUR ===

	@Override
	public void onGameStarted(int nbPlayers, int mode, boolean extensionActive) {
		System.out.println("\n✅ Partie démarrée !");
		System.out.println("Joueurs : " + nbPlayers);
		String modeStr = mode == 0 ? "Classique" : (mode == 1 ? "Bouffon" : "Clair");
		System.out.println("Mode : " + modeStr);
		System.out.println("Extension : " + (extensionActive ? "Activée" : "Désactivée"));
	}

	@Override
	public void onRoundStarted(int roundNumber) {
		clearScreen();
		System.out.println("\n====== 🎮 ROUND " + roundNumber + " 🎮 ======");
	}

	@Override
	public void onCardsDistributed(List<Player> players) {
		System.out.println("\n✅ Cartes distribuées à tous les joueurs.");
	}

	@Override
	public void onPlayerTurnToOffer(Player player) {
		clearScreen();
		System.out.println("\n====== Tour de " + player.getName() + " ======");

		// Afficher UNIQUEMENT la main du joueur actif pendant la phase d'offre
		if (player.getHand().size() >= 2) {
			System.out.println("Votre main : ");
			System.out.println("(1) " + player.getHand().get(0));
			System.out.println("(2) " + player.getHand().get(1));
		}
	}

	@Override
	public void onOfferMade(Player player, Offer offer) {
		// Ne plus afficher la main après avoir fait l'offre
		// Seulement indiquer que l'offre a été faite
		System.out.println(player.getName() + " a fait son offre (carte visible: " + offer.getVisibleCard()
				+ " / carte cachée: ???)");
		waitForUser();
	}

	@Override
	public void onPlayerTurnToChoose(Player player, List<Offer> availableOffers) {
		clearScreen();
		System.out.println("\n====== " + player.getName() + " doit choisir une carte ======");

		if (availableOffers.isEmpty()) {
			System.out.println("Aucune offre disponible, vous prenez votre propre carte.");
		} else {
			System.out.println("\nOffres disponibles :");
			int index = 0;
			for (Offer o : availableOffers) {
				System.out.println("(" + (index + 1) + ") " + o.getOwner().getName() + " - Visible: "
						+ o.getVisibleCard() + " / Cachée: ???");
				index++;
			}
		}
	}

	/**
	 * Notifie l'utilisateur qu'une carte a été choisie par un adversaire.
	 * 
	 * @param chooser Le joueur effectuant l'action.
	 * @param target  Le joueur dont la carte est prise.
	 * @param card    La carte qui a été piochée.
	 */

	@Override
	public void onCardChosen(Player chooser, Player target, Card card) {
		if (chooser == target) {
			System.out.println(chooser.getName() + " prend de sa main " + card);
		} else {
			System.out.println(chooser.getName() + " prend " + card + " depuis " + target.getName());
		}
		waitForUser();
	}

	@Override
	public void onRoundEnded(List<Player> players) {
		System.out.println("\n====== Fin du round ======");
		System.out.println("\nJest des joueurs :");
		for (Player p : players) {
			System.out.println(p.getName() + " : " + p.getJest());
		}
		waitForUser();

		// La sauvegarde est gérée par la GUI via popup, pas par la console
		// pour éviter le blocage
	}

	@Override
	public void onGameEnded(Player winner, List<Player> players, int[] scores) {
		clearScreen();
		System.out.println("\n ====== 🏆 FIN DE LA PARTIE 🏆 =====");

		System.out.println("\n ====== 📈 SCORES 📈 =====");
		for (int i = 0; i < players.size(); i++) {
			System.out.println(players.get(i).getName() + " : " + scores[i] + " points");
		}

		System.out.println("\n ====== 👑 GAGNANT 👑 =====");
		System.out.println("Félicitations à " + winner.getName() + " 👑 qui remporte le jeu !");

		// La sauvegarde sera gérée par la popup GUI pour ne pas bloquer
		System.out.println("\n(La sauvegarde peut être effectuée via l'interface graphique)");
	}

	@Override
	public void onTrophyCardsDisplayed(Card trophy1, Card trophy2) {
		System.out.println("\n====== TROPHEES =====");
		System.out.println(trophy1);
		if (trophy2 != null) {
			System.out.println(trophy2);
		}
		System.out.println("=====================\n");
	}

	@Override
	public void onStackDisplayed(List<Card> stack) {
		System.out.println("👁️ JEST Clair : Le stack est composé de :");
		for (Card s : stack) {
			System.out.println(s.getColor() + " " + s.getFaceValue());
		}
		System.out.println("\n");
	}

	@Override
	public void onGameSaved(String saveName) {
		System.out.println("✅ Partie sauvegardée avec succès : " + saveName);
	}

	@Override
	public void onGameLoaded(String saveName) {
		System.out.println("✅ Partie chargée avec succès : " + saveName);
	}

	@Override
	public void onError(String errorMessage) {
		System.out.println("❌ ERREUR : " + errorMessage);
	}

	// === MÉTHODES UTILITAIRES ===

	private void clearScreen() {
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}

	private void waitForUser() {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
