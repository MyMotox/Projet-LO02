package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import model.Card;
import model.ClassicCounter;
import model.Color;
import model.Deck;
import model.GameState;
import model.Offer;
import model.Player;
import model.RealPlayer;
import model.SaveLoadManager;
import model.Trophy;
import model.TrophyManager;
import model.VirtualPlayerCheater;
import model.VirtualPlayerRandom;

/**
 * Classe de gestion d'une partie de Jest en mode console pur. Contient toute la
 * logique de jeu sans utiliser le pattern MVC. Cette classe obsolète a été laissée pour montrer l'évolution du projet.
 * 
 * @deprecated Utiliser GameController avec le pattern MVC
 */
@Deprecated
public class PartieJest {

	private int nbPlayers;
	private List<Player> players;
	private List<Offer> offers;
	private Deck deck;

	private Card trophyCard1;
	private Card trophyCard2;

	private TrophyManager tm;
	private ClassicCounter counter;

	private List<Card> stack = new ArrayList<>();

	private static final Scanner sc = new Scanner(System.in);

	private int mode = 0;

	private boolean extensionActive = false;

	/**
	 * Constructeur de partie Jest. Affiche le menu principal et propose de démarrer
	 * une nouvelle partie ou charger une sauvegarde.
	 */
	public PartieJest() {
		System.out.println("\n" + "█████████████████████████\n" + "███▄─▄█▄─▄▄─█─▄▄▄▄█─▄─▄─█\n"
				+ "█─▄█─███─▄█▀█▄▄▄▄─███─███\n" + "▀▄▄▄▀▀▀▄▄▄▄▄▀▄▄▄▄▄▀▀▄▄▄▀▀\n");

		// Demander si on veut charger une partie
		System.out.println("♻️ Voulez-vous : (1) Nouvelle partie | (2) Charger une partie");
		int choixLoad = sc.nextInt();

		if (choixLoad == 2) {
			loadAndContinueGame();
			return;
		}

		// Nouvelle partie
		startNewGame();
	}

	/**
	 * Démarre une nouvelle partie. Configure le nombre de joueurs, l'activation de
	 * l'extension, initialise le deck et démarre les rounds.
	 */
	private void startNewGame() {
		System.out.println("🃏 Souhaitez-vous activer l'extension Carte Bonus/Malus/Gold ? (0) Non | (1) Oui ");
		int choixExt = sc.nextInt();

		if (choixExt == 1) {
			extensionActive = true;
			System.out.println("✅ Extension Carte Bonus/Malus/Gold activée !");
		} else {
			System.out.println("❌ Extension désactivée.");
		}

		System.out.println("🧑‍🧑‍🧒‍🧒 Choisissez le nombre de joueurs Humain (max 4) :");

		players = new ArrayList<>();

		int nbHumanPlayers = sc.nextInt();

		if (nbHumanPlayers < 0) {
			nbHumanPlayers = 0;
		}

		if (nbHumanPlayers > 4) {
			nbHumanPlayers = 4;
		}

		nbPlayers = nbHumanPlayers;

		if (nbHumanPlayers < 4) {
			System.out.println("🤖 Choisissez le nombre de robot (max " + (4 - nbHumanPlayers) + ")");
			int nbBotPlayers = sc.nextInt();

			if (nbBotPlayers < 0) {
				nbBotPlayers = 0;
			}

			if (nbBotPlayers > (4 - nbHumanPlayers)) {
				nbBotPlayers = (4 - nbHumanPlayers);
			}

			nbPlayers = nbHumanPlayers + nbBotPlayers;

			if (nbPlayers < 2) {
				nbBotPlayers = 2 - nbHumanPlayers;
				nbPlayers = nbHumanPlayers + nbBotPlayers;
			}

			for (int i = 0; i < nbBotPlayers; i++) {
				System.out.println("🤖 Souhaitez-vous rajouter pour le robot " + (i + 1)
						+ " une stratégie aléatoire ou un robot tricheur? (1) Robot Aléatoire | (2) Robot Tricheur");
				int Choix = sc.nextInt();
				if (Choix == 1) {
					players.add(new Player("Bender-" + (i + 1), new VirtualPlayerRandom()));
					System.out.println("🤪 Bender-" + (i + 1) + " le robot imprévisible a été rajouté avec succès !");
				} else {
					players.add(new Player("HAL-9000-" + (i + 1), new VirtualPlayerCheater()));
					System.out.println("🛸 HAL-9000-" + (i + 1) + " le robot manipulateur a été rajouté avec succès !");
				}
			}

		}

		System.out.println("🕹 ️Choisissez votre mode de jeu :");
		System.out.println("(1) ☕️ JEST classique - Règles classiques de JEST");
		System.out.println(
				"(2) 👁️ JEST Bouffon - Le joueur avec la moins bonne main devient bouffon et commence le round");
		System.out.println("(3) 🎭 JEST Clair - Le stack est affiché avant la distribution des cartes");
		int choixMode = sc.nextInt();

		if (choixMode == 2) {
			mode = 1;
			System.out.println("🎭 Vous avez choisi le mode JEST Bouffon !");
		} else if (choixMode == 3) {
			mode = 2;
			System.out.println("👁 Vous avez choisi le mode JEST Clair !");
		}

		for (int i = 0; i < nbHumanPlayers; i++) {
			System.out.println("📝 Entrez le nom du joueur " + (i + 1) + " :");
			String name = sc.next();
			players.add(new Player(name, new RealPlayer()));
		}

		// initialiser le deck et les cartes
		deck = new Deck();
		deck.initStandardDeck(extensionActive);

		offers = new ArrayList<>();

		// Tirage des cartes trophée selon le nombre de joueurs
		trophyCard1 = deck.draw();
		if (extensionActive) {
			while (trophyCard1.getColor() == Color.BONUS || trophyCard1.getColor() == Color.MALUS
					|| trophyCard1.getColor() == Color.GOLD) {
				trophyCard1 = deck.draw();
			}
		}

		// En partie à 4 joueurs: 1 seul trophée
		// En partie à 3 joueurs: 2 trophées&
		if (players.size() == 4) {
			System.out.println("Partie à 4 joueurs : 1 trophée");
			tm = new TrophyManager(trophyCard1, null);
		} else {
			trophyCard2 = deck.draw();
			if (extensionActive) {
				while (trophyCard2.getColor() == Color.BONUS || trophyCard2.getColor() == Color.MALUS
						|| trophyCard2.getColor() == Color.GOLD) {
					trophyCard2 = deck.draw();
				}
			}
			System.out.println("Partie à " + nbPlayers + " joueurs : 2 trophées");
			tm = new TrophyManager(trophyCard1, trophyCard2);
		}

		counter = new ClassicCounter();

		// Boucle du jeu
		runGameLoop();
	}

	private void loadAndContinueGame() {
		List<String> saves = SaveLoadManager.listSaves();

		if (saves.isEmpty()) {
			System.out.println("📝 Aucune sauvegarde trouvée. Démarrage d'une nouvelle partie...");
			startNewGame();
			return;
		}

		System.out.println("♻️ Sauvegardes disponibles :");
		for (int i = 0; i < saves.size(); i++) {
			System.out.println((i + 1) + ". " + saves.get(i));
		}
		System.out.println("📝 Choisissez une sauvegarde (numéro) :");
		int choice = sc.nextInt() - 1;

		if (choice < 0 || choice >= saves.size()) {
			System.out.println("❌ Choix invalide. Démarrage d'une nouvelle partie...");
			startNewGame();
			return;
		}

		GameState gameState = SaveLoadManager.loadGame(saves.get(choice));

		if (gameState == null) {
			System.out.println("❌ Erreur lors du chargement. Démarrage d'une nouvelle partie...");
			startNewGame();
			return;
		}

		// Restaurer l'état du jeu
		restoreGameState(gameState);

		System.out.println("✅ Partie chargée avec succès ! Reprise du jeu...");
		waiting();

		// Continuer la boucle de jeu
		runGameLoop();
	}

	private void runGameLoop() {
		while ((deck.size() + stack.size()) >= (players.size() * 2)) {
			// Proposer de sauvegarder avant chaque round
			System.out.println("♻️ Voulez-vous sauvegarder la partie ? (0) Non | (1) Oui");
			int choixSave = sc.nextInt();
			if (choixSave == 1) {
				saveCurrentGame();
			}

			// distribution initiale de 2 cartes par Joueur
			distributeCards();
			playRound(); // lance un cycle de jeu
		}

		// Attribution des trophées :
		tm.assignAll(players, counter);

		// Ajouter les cartes trophées dans les Jest des gagnants
		for (Trophy t : tm.getTrophies()) {
			if (t.getOwner() != null) {
				t.getOwner().toJest(t.getSourceCard());
			}
		}

		System.out.println("\n ====== 🏆 TROPHEES 🏆 =====");
		for (Trophy t : tm.getTrophies()) {
			System.out.println(t);
		}

		System.out.println("\n ====== 📈 SCORES 📈 =====");
		for (Player p : players) {
			System.out.println(p.getName() + " : " + counter.visit(p));
		}

		System.out.println("\n ====== 👑 GAGNANT 👑 =====");
		int points = -99;
		Player winner = null;
		for (Player p : players) {
			if (counter.visit(p) > points) {
				points = counter.visit(p);
				winner = p;
			}
		}
		System.out.println(
				"Félicitations à " + winner.getName() + " 👑 qui remporte le jeu avec " + points + " points !");
	}

	// Sauvegarde la partie en cours
	private void saveCurrentGame() {
		System.out.println("♻️ Nom de la sauvegarde :");
		String saveName = sc.next();

		GameState gameState = captureGameState();

		if (SaveLoadManager.saveGame(gameState, saveName)) {
			System.out.println("✅ Partie sauvegardée avec succès !");
		} else {
			System.out.println("❌ Erreur lors de la sauvegarde.");
		}

		waiting();
	}

	// Capture l'état actuel du jeu
	private GameState captureGameState() {
		GameState gs = new GameState();

		// Configuration
		gs.setNbPlayers(nbPlayers);
		gs.setMode(mode);
		gs.setExtensionActive(extensionActive);

		// Joueurs
		for (Player p : players) {
			gs.getPlayerStates().add(SaveLoadManager.toPlayerState(p));
		}

		// Deck
		for (Card c : deck.getDeck()) {
			gs.getDeckCards().add(SaveLoadManager.toCardState(c));
		}

		// Stack
		for (Card c : stack) {
			gs.getStackCards().add(SaveLoadManager.toCardState(c));
		}

		// Cartes trophées
		gs.setTrophyCard1(SaveLoadManager.toCardState(trophyCard1));
		gs.setTrophyCard2(SaveLoadManager.toCardState(trophyCard2));

		// Offres
		for (Offer o : offers) {
			gs.getCurrentOffers().add(SaveLoadManager.toOfferState(o, players));
		}

		// Trophées
		for (Trophy t : tm.getTrophies()) {
			gs.getTrophies().add(SaveLoadManager.toTrophyState(t, players));
		}

		return gs;
	}

	// Restaure l'état du jeu depuis un GameState
	private void restoreGameState(GameState gs) {
		// Configuration
		nbPlayers = gs.getNbPlayers();
		mode = gs.getMode();
		extensionActive = gs.isExtensionActive();

		// Joueurs
		players = new ArrayList<>();
		for (GameState.PlayerState ps : gs.getPlayerStates()) {
			players.add(SaveLoadManager.toPlayer(ps));
		}

		// Deck
		deck = new Deck();
		for (GameState.CardState cs : gs.getDeckCards()) {
			deck.add(SaveLoadManager.toCard(cs));
		}

		// Stack
		stack = new ArrayList<>();
		for (GameState.CardState cs : gs.getStackCards()) {
			stack.add(SaveLoadManager.toCard(cs));
		}

		// Cartes trophées
		trophyCard1 = SaveLoadManager.toCard(gs.getTrophyCard1());
		trophyCard2 = SaveLoadManager.toCard(gs.getTrophyCard2());

		// TrophyManager
		tm = new TrophyManager(trophyCard1, trophyCard2);

		// Restaurer les trophées (notamment leurs propriétaires)
		List<Trophy> loadedTrophies = new ArrayList<>();
		for (GameState.TrophyState ts : gs.getTrophies()) {
			loadedTrophies.add(SaveLoadManager.toTrophy(ts, players));
		}
		// Remplacer les trophées du TrophyManager
		tm.getTrophies().clear();
		tm.getTrophies().addAll(loadedTrophies);

		// Counter
		counter = new ClassicCounter();

		// Offres
		offers = new ArrayList<>();
		for (GameState.OfferState os : gs.getCurrentOffers()) {
			offers.add(SaveLoadManager.toOffer(os, players));
		}
	}

	// distribution 2 cartes à chaque joueur
	private void distributeCards() {
		// --- 1er round ---
		if (stack.isEmpty()) {
			for (Player p : players) {
				p.toHand(deck.draw());
				p.toHand(deck.draw());
			}
			return;
		}

		// --- Rounds suivants ---
		int cardsNeeded = players.size() * 2;
		int cardsToAdd = cardsNeeded - stack.size();

		for (int i = 0; i < cardsToAdd && !deck.isEmpty(); i++) {
			stack.add(deck.draw());
		}

		Collections.shuffle(stack);

		int i = 0;
		for (Player p : players) {
			p.toHand(stack.get(i));
			i++;
			p.toHand(stack.get(i));
			i++;
			p.SetHasNotPlayed();
		}
	}

	public Player chooseFirst() {
		Player first = null;
		int maxValue = -1;

		for (Offer o : offers) {
			if (o.getVisibleCard() != null) {
				int value = o.getVisibleCard().getFaceValue();
				if (value > maxValue) {
					maxValue = value;
					first = o.getOwner();
				}
			}
		}

		return first;
	}

	public Player chooseLast() {
		Player last = null;
		int min = 999;
		for (Player p : players) {
			int priority = p.getHand().get(0).getFaceValue() + p.getHand().get(1).getFaceValue();
			if (priority < min) {
				min = priority;
				last = p;
			}
		}
		return last;
	}

	// Round complet
	public void playRound() {

		// PHASE D'OFFRE
		offers.clear();

		for (Player p : players) {
			waiting(p);
			p.SetHasNotPlayed();

			System.out.println("====== TROPHEES =====");
			System.out.println(trophyCard1);
			if (trophyCard2 != null) {
				System.out.println(trophyCard2);
			}
			System.out.println("=====================\n");

			if (mode == 2) {

				if (stack.isEmpty()) {
					System.out.println("👁️ JEST Clair : Le stack est pour l'instant vide...");
				} else {
					System.out.println("👁️ JEST Clair : Le stack est composé de :");
					for (Card s : stack) {
						System.out.println(s.getColor() + " " + s.getFaceValue());
					}
				}

				System.out.println("\n");

			}

			p.getStrategy().displayHand(p);
			Offer offer = p.getStrategy().offer(p);
			if (offer != null) {
				offers.add(offer);
				System.out.println(p.getName() + " a proposé " + offer.getVisibleCard() + " / HIDDEN");
			}
			if (p.getStrategy() instanceof VirtualPlayerRandom || p.getStrategy() instanceof VirtualPlayerCheater) {
				waiting();
			}
		}

		// PHASE DE CHOIX
		Player current = chooseFirst();

		if (mode == 1) {
			System.out.println("🎭 JEST Bouffon : " + chooseLast().getName() + " est le bouffon !");
			current = chooseLast();
			waiting();
		}

		for (int i = 0; i < players.size(); i++) {
			space();

			List<Offer> available = new ArrayList<>();
			for (Offer o : offers) {
				if (o.getOwner() != current && o.getVisibleCard() != null && o.getHiddenCard() != null) {
					available.add(o);
				}
			}
			space();

			int index = 0;
			for (Offer a : available) {
				System.out.println(
						"(" + (index + 1) + ") " + a.getOwner() + " - " + a.getVisibleCard() + " / " + "HIDDEN");
				index++;
			}

			if (available.isEmpty()) {
				System.out.println(current.getName() + " : aucune offre complète disponible, prends ta propre carte.");

				Offer ownOffer = current.getOffer(offers);
				System.out.println("Votre main : " + ownOffer.getVisibleCard() + " (1) / HIDDEN (2)");
				Card taken = current.getStrategy().choose(current, ownOffer, stack);

				current.toJest(taken);
				System.out.println(current.getName() + " prend de sa main " + taken);
				current.SetHasPlayed();
				if (current.getStrategy() instanceof VirtualPlayerRandom
						|| current.getStrategy() instanceof VirtualPlayerCheater) {
					waiting();
				}
				continue;
			}

			if (!(current.hasPlayed())) {

				Player target = current.getStrategy().chooseTarget(current, available);
				for (Offer o : offers) {
					if (o.getOwner() == target) {
						System.out.println(target.getName() + " a proposé " + o.getVisibleCard() + " (1) / HIDDEN (2)");
					}
				}

				if (target == null) {
					Offer chosen = available.get(0);
					Card taken = chosen.getVisibleCard();
					current.toJest(taken);
					chosen.removeVisibleCard();
					System.out
							.println(current.getName() + " prend " + taken + " depuis " + chosen.getOwner().getName());
					if (current.getStrategy() instanceof VirtualPlayerRandom
							|| current.getStrategy() instanceof VirtualPlayerCheater) {
						waiting();
					}

				} else {
					Offer targetOffer = null;
					for (Offer o : offers) {
						if (o.getOwner() == target) {
							targetOffer = o;
							break;
						}
					}
					if (targetOffer != null) {
						Card taken = current.getStrategy().choose(current, targetOffer, stack);
						if (taken != null) {
							current.toJest(taken);
							System.out.println(current.getName() + " prend " + taken + " depuis " + target.getName());
							if (current.getStrategy() instanceof VirtualPlayerRandom
									|| current.getStrategy() instanceof VirtualPlayerCheater) {
								waiting();
							}
						}
					}
				}

				current.SetHasPlayed();
				if (target.hasPlayed()) {
					for (Player p : players) {
						if (!(p.hasPlayed())) {
							current = p;
						}
					}
				} else {
					current = target;
				}

				if (!(i < players.size())) {
					waiting(current);
				}

			}
		}

		// == FIN DE ROUND ==

		stack.clear();

		for (Offer o : offers) {
			Player owner = o.getOwner();
			Card remaining = o.getRemainingCard();
			stack.add(remaining);
		}

		boolean lastRound = (deck.size() + stack.size()) < players.size() * 2;

		for (Offer o : offers) {
			Player owner = o.getOwner();
			Card remaining = o.getRemainingCard();
			if (lastRound) {
				if (remaining != null) {
					owner.toJest(remaining);
				}
			}

			o.clearOffer();
		}
		for (Player p : players) {
			System.out.println(p.getJest());
			p.getHand().clear();
		}

		space();
		System.out.println("Fin du round");
		waiting();
	}

	public void space() {
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}

	public void waiting(Player next) {
		space();
		System.out.println("Au tour de " + next.getName());
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		space();
	}

	public void waiting() {
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}