package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import model.Card;
import model.ClassicCounter;
import model.Color;
import model.Deck;
import model.GameState;
import model.Offer;
import model.Player;
import model.SaveLoadManager;
import model.Strategy;
import model.Trophy;
import model.TrophyManager;
import model.VirtualPlayerCheater;
import model.VirtualPlayerRandom;
import view.GameObserver;

/**
 * Contrôleur principal gérant la machine à états du jeu Jest.
 * <p>
 * Cette classe orchestre les tours de jeu, la distribution, et notifie les vues
 * via l'interface {@link view.GameObserver}. Elle assure la séparation entre 
 * les données métier et l'affichage.
 * </p>
 * @author Mathéo A.
 * @author Jules CS
 * @version 1.0
 * @see model.GameState
 */
public class GameController {

	// Modèle
	private int nbPlayers;
	private List<Player> players;
	private List<Offer> offers;
	private Deck deck;
	private Card trophyCard1;
	private Card trophyCard2;
	private TrophyManager tm;
	private ClassicCounter counter;
	private List<Card> stack;
	private int mode = 0;
	private boolean extensionActive = false;
	private int currentRound = 0;

	// Observateurs (vues)
	private List<GameObserver> observers;

	// Stratégie pour les joueurs humains (sera définie par les vues)
	private Strategy humanStrategy;

	/**
	 * Construit un contrôleur de jeu vide.
	 * Initialise les listes de joueurs, offres, stack et observateurs.
	 */
	public GameController() {
		this.observers = new ArrayList<>();
		this.stack = new ArrayList<>();
		this.offers = new ArrayList<>();
		this.players = new ArrayList<>();
	}

	/**
     * Enregistre un nouvel observateur pour recevoir les notifications de changement d'état.
     * @param observer La vue (Console ou GUI) à attacher au contrôleur.
     */
	public void addObserver(GameObserver observer) {
		observers.add(observer);
	}

	/**
	 * Retire un observateur du contrôleur.
	 * 
	 * @param observer l'observateur à retirer
	 */
	public void removeObserver(GameObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Notifie tous les observateurs que la partie a démarré.
	 */
	private void notifyGameStarted() {
		for (GameObserver observer : observers) {
			observer.onGameStarted(nbPlayers, mode, extensionActive);
		}
	}

	/**
	 * Notifie tous les observateurs qu'un nouveau round a démarré.
	 */
	private void notifyRoundStarted() {
		for (GameObserver observer : observers) {
			observer.onRoundStarted(currentRound);
		}
	}

	/**
	 * Notifie tous les observateurs que les cartes ont été distribuées.
	 */
	private void notifyCardsDistributed() {
		for (GameObserver observer : observers) {
			observer.onCardsDistributed(players);
		}
	}

	/**
	 * Notifie tous les observateurs qu'un joueur doit créer son offre.
	 * 
	 * @param player le joueur dont c'est le tour
	 */
	private void notifyPlayerTurnToOffer(Player player) {
		for (GameObserver observer : observers) {
			observer.onPlayerTurnToOffer(player);
		}
	}

	/**
	 * Notifie tous les observateurs qu'un joueur a créé son offre.
	 * 
	 * @param player le joueur qui a créé l'offre
	 * @param offer l'offre créée
	 */
	private void notifyOfferMade(Player player, Offer offer) {
		for (GameObserver observer : observers) {
			observer.onOfferMade(player, offer);
		}
	}

	/**
	 * Notifie tous les observateurs qu'un joueur doit choisir une offre.
	 * 
	 * @param player le joueur dont c'est le tour de choisir
	 */
	private void notifyPlayerTurnToChoose(Player player) {
		List<Offer> available = getAvailableOffers(player);
		for (GameObserver observer : observers) {
			observer.onPlayerTurnToChoose(player, available);
		}
	}

	private void notifyCardChosen(Player chooser, Player target, Card card) {
		for (GameObserver observer : observers) {
			observer.onCardChosen(chooser, target, card);
		}
	}

	private void notifyRoundEnded() {
		for (GameObserver observer : observers) {
			observer.onRoundEnded(players);
		}
	}

	private void notifyGameEnded() {
		Player winner = determineWinner();
		int[] scores = calculateScores();
		for (GameObserver observer : observers) {
			observer.onGameEnded(winner, players, scores);
		}
	}

	private void notifyTrophyCardsDisplayed() {
		for (GameObserver observer : observers) {
			observer.onTrophyCardsDisplayed(trophyCard1, trophyCard2);
		}
	}

	private void notifyStackDisplayed() {
		for (GameObserver observer : observers) {
			observer.onStackDisplayed(stack);
		}
	}

	private void notifyGameSaved(String saveName) {
		for (GameObserver observer : observers) {
			observer.onGameSaved(saveName);
		}
	}

	private void notifyGameLoaded(String saveName) {
		for (GameObserver observer : observers) {
			observer.onGameLoaded(saveName);
		}
	}

	private void notifyError(String message) {
		for (GameObserver observer : observers) {
			observer.onError(message);
		}
	}

	// === CONFIGURATION DU JEU ===

	public void setHumanStrategy(Strategy strategy) {
		this.humanStrategy = strategy;
	}
	
	/**
     * Initialise une nouvelle partie avec les paramètres fournis.
     * @param nbPlayers Nombre de joueurs participants.
     * @param mode Mode de jeu choisi.
     * @param extensionActive Indique si les cartes spéciales (Bonus/Malus/Gold) sont incluses.
     */

	public void startNewGame(int nbHumanPlayers, int nbBotPlayers, List<String> playerNames,
			List<Boolean> botTypes, int mode, boolean extensionActive) {

		this.mode = mode;
		this.extensionActive = extensionActive;
		this.nbPlayers = nbHumanPlayers + nbBotPlayers;
		this.players = new ArrayList<>();
		this.currentRound = 0;

		// Créer les joueurs humains
		for (int i = 0; i < nbHumanPlayers; i++) {
			String name = (playerNames != null && i < playerNames.size()) ? playerNames.get(i)
					: "Joueur " + (i + 1);
			players.add(new Player(name, humanStrategy));
		}

		// Créer les bots
		for (int i = 0; i < nbBotPlayers; i++) {
			boolean isCheater = (botTypes != null && i < botTypes.size()) ? botTypes.get(i) : false;
			if (isCheater) {
				players.add(new Player("HAL-9000-" + (i + 1), new VirtualPlayerCheater()));
			} else {
				players.add(new Player("Bender-" + (i + 1), new VirtualPlayerRandom()));
			}
		}

		// Initialiser le deck
		deck = new Deck();
		deck.initStandardDeck(extensionActive);

		// Tirer les cartes trophées
		trophyCard1 = deck.draw();
		if (extensionActive) {
			while (trophyCard1.getColor() == Color.BONUS || trophyCard1.getColor() == Color.MALUS
					|| trophyCard1.getColor() == Color.GOLD) {
				trophyCard1 = deck.draw();
			}
		}

		if (players.size() == 4) {
			tm = new TrophyManager(trophyCard1, null);
		} else {
			trophyCard2 = deck.draw();
			if (extensionActive) {
				while (trophyCard2.getColor() == Color.BONUS || trophyCard2.getColor() == Color.MALUS
						|| trophyCard2.getColor() == Color.GOLD) {
					trophyCard2 = deck.draw();
				}
			}
			tm = new TrophyManager(trophyCard1, trophyCard2);
		}

		counter = new ClassicCounter();
		offers = new ArrayList<>();

		notifyGameStarted();
	}

	public void loadGame(String saveName) {
		GameState gameState = SaveLoadManager.loadGame(saveName);

		if (gameState == null) {
			notifyError("Erreur lors du chargement de la partie");
			return;
		}

		restoreGameState(gameState);
		notifyGameLoaded(saveName);
	}

	public List<String> getAvailableSaves() {
		return SaveLoadManager.listSaves();
	}

	// === BOUCLE DE JEU ===

	public void runGameLoop() {
		while ((deck.size() + stack.size()) >= (players.size() * 2)) {
			currentRound++;
			distributeCards();
			playRound();
		}

		// Fin de partie
		tm.assignAll(players, counter);

		// Ajouter les cartes trophées dans les Jest des gagnants
		for (Trophy t : tm.getTrophies()) {
			if (t.getOwner() != null) {
				t.getOwner().toJest(t.getSourceCard());
			}
		}

		notifyGameEnded();
	}

	private void distributeCards() {
		// Si les joueurs ont déjà des cartes (chargement de partie), ne pas redistribuer
		boolean playersHaveCards = players.stream().anyMatch(p -> !p.getHand().isEmpty());
		if (playersHaveCards) {
			return;
		}
		
		if (stack.isEmpty()) {
			for (Player p : players) {
				p.toHand(deck.draw());
				p.toHand(deck.draw());
			}
		} else {
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

		notifyCardsDistributed();
	}

	private void playRound() {
		notifyRoundStarted();
		
		// Vérifier si les offres contiennent des cartes valides (pour le chargement de partie)
		// Ne pas confondre avec des offres vides du round précédent
		boolean offersAlreadyExist = !offers.isEmpty() && 
			offers.stream().anyMatch(o -> o.getVisibleCard() != null || o.getHiddenCard() != null);
		
		if (!offersAlreadyExist) {
			offers.clear();

			// PHASE D'OFFRE
			notifyTrophyCardsDisplayed();

			if (mode == 2 && !stack.isEmpty()) {
				notifyStackDisplayed();
			}

			for (Player p : players) {
				p.SetHasNotPlayed();
				notifyPlayerTurnToOffer(p);

				Offer offer = p.getStrategy().offer(p);
// Pause AVANT pour les bots - pour laisser l'utilisateur voir leur main
if (p.getStrategy() instanceof VirtualPlayerRandom || p.getStrategy() instanceof VirtualPlayerCheater) {
try {
TimeUnit.MILLISECONDS.sleep(800);
} catch (InterruptedException e) {
e.printStackTrace();
}
}
				if (offer != null) {
					offers.add(offer);
					notifyOfferMade(p, offer);
				}

			// Pause pour les bots
			if (p.getStrategy() instanceof VirtualPlayerRandom || p.getStrategy() instanceof VirtualPlayerCheater) {
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		} else {
			// Partie chargée avec offres existantes - afficher l'état
			notifyTrophyCardsDisplayed();
			if (mode == 2 && !stack.isEmpty()) {
				notifyStackDisplayed();
			}
		}

		// PHASE DE CHOIX
		Player current = chooseFirst();

		if (mode == 1) {
			current = chooseLast();
		}

		for (int i = 0; i < players.size(); i++) {
			List<Offer> available = getAvailableOffers(current);

			if (available.isEmpty()) {
				// Le joueur prend sa propre carte
				Offer ownOffer = current.getOffer(offers);
				Card taken = current.getStrategy().choose(current, ownOffer, stack);
				current.toJest(taken);
				notifyCardChosen(current, current, taken);
				current.SetHasPlayed();

				if (current.getStrategy() instanceof VirtualPlayerRandom
						|| current.getStrategy() instanceof VirtualPlayerCheater) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				continue;
			}

			if (!current.hasPlayed()) {
				notifyPlayerTurnToChoose(current);

				Player target = current.getStrategy().chooseTarget(current, available);

				if (target == null) {
					target = available.get(0).getOwner();
				}

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
						notifyCardChosen(current, target, taken);

						if (current.getStrategy() instanceof VirtualPlayerRandom
								|| current.getStrategy() instanceof VirtualPlayerCheater) {
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}

				current.SetHasPlayed();

				if (target.hasPlayed()) {
					for (Player p : players) {
						if (!p.hasPlayed()) {
							current = p;
							break;
						}
					}
				} else {
					current = target;
				}
			}
		}

		// FIN DE ROUND
		stack.clear();

		for (Offer o : offers) {
			Card remaining = o.getRemainingCard();
			if (remaining != null) {
				stack.add(remaining);
			}
		}

		boolean lastRound = (deck.size() + stack.size()) < players.size() * 2;

		if (lastRound) {
			for (Offer o : offers) {
				Player owner = o.getOwner();
				Card remaining = o.getRemainingCard();
				if (remaining != null) {
					owner.toJest(remaining);
				}
			}
		}

		for (Offer o : offers) {
			o.clearOffer();
		}

		for (Player p : players) {
			p.getHand().clear();
		}

		notifyRoundEnded();
	}

	// === MÉTHODES UTILITAIRES ===

	private List<Offer> getAvailableOffers(Player current) {
		List<Offer> available = new ArrayList<>();
		for (Offer o : offers) {
			if (o.getOwner() != current && o.getVisibleCard() != null && o.getHiddenCard() != null) {
				available.add(o);
			}
		}
		return available;
	}

	private Player chooseFirst() {
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

	private Player chooseLast() {
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

	private Player determineWinner() {
		int maxPoints = -999;
		Player winner = null;
		for (Player p : players) {
			int points = counter.visit(p);
			if (points > maxPoints) {
				maxPoints = points;
				winner = p;
			}
		}
		return winner;
	}

	private int[] calculateScores() {
		int[] scores = new int[players.size()];
		for (int i = 0; i < players.size(); i++) {
			scores[i] = counter.visit(players.get(i));
		}
		return scores;
	}

	// === SAUVEGARDE/CHARGEMENT ===

	public void saveGame(String saveName) {
		GameState gameState = captureGameState();

		if (SaveLoadManager.saveGame(gameState, saveName)) {
			notifyGameSaved(saveName);
		} else {
			notifyError("Erreur lors de la sauvegarde");
		}
	}

	private GameState captureGameState() {
		GameState gs = new GameState();

		gs.setNbPlayers(nbPlayers);
		gs.setMode(mode);
		gs.setExtensionActive(extensionActive);
		gs.setCurrentRound(currentRound);

		for (Player p : players) {
			gs.getPlayerStates().add(SaveLoadManager.toPlayerState(p));
		}

		for (Card c : deck.getDeck()) {
			gs.getDeckCards().add(SaveLoadManager.toCardState(c));
		}

		for (Card c : stack) {
			gs.getStackCards().add(SaveLoadManager.toCardState(c));
		}

		gs.setTrophyCard1(SaveLoadManager.toCardState(trophyCard1));
		gs.setTrophyCard2(SaveLoadManager.toCardState(trophyCard2));

		for (Offer o : offers) {
			gs.getCurrentOffers().add(SaveLoadManager.toOfferState(o, players));
		}

		for (Trophy t : tm.getTrophies()) {
			gs.getTrophies().add(SaveLoadManager.toTrophyState(t, players));
		}

		return gs;
	}

	private void restoreGameState(GameState gs) {
		nbPlayers = gs.getNbPlayers();
		mode = gs.getMode();
		extensionActive = gs.isExtensionActive();
		currentRound = gs.getCurrentRound();

		players = new ArrayList<>();
		for (GameState.PlayerState ps : gs.getPlayerStates()) {
			Player p = SaveLoadManager.toPlayer(ps);
			// Restaurer la stratégie appropriée pour les joueurs humains
			String stratType = ps.getStrategyType();
			if (stratType != null && (stratType.equals("REAL") || stratType.equals("RealPlayer"))) {
				p = new Player(ps.getName(), humanStrategy);
			}
			players.add(p);
		}

		deck = new Deck();
		for (GameState.CardState cs : gs.getDeckCards()) {
			deck.add(SaveLoadManager.toCard(cs));
		}

		stack = new ArrayList<>();
		for (GameState.CardState cs : gs.getStackCards()) {
			stack.add(SaveLoadManager.toCard(cs));
		}

		trophyCard1 = SaveLoadManager.toCard(gs.getTrophyCard1());
		trophyCard2 = SaveLoadManager.toCard(gs.getTrophyCard2());

		tm = new TrophyManager(trophyCard1, trophyCard2);

		List<Trophy> loadedTrophies = new ArrayList<>();
		for (GameState.TrophyState ts : gs.getTrophies()) {
			loadedTrophies.add(SaveLoadManager.toTrophy(ts, players));
		}
		tm.getTrophies().clear();
		tm.getTrophies().addAll(loadedTrophies);

		counter = new ClassicCounter();

		offers = new ArrayList<>();
		for (GameState.OfferState os : gs.getCurrentOffers()) {
			offers.add(SaveLoadManager.toOffer(os, players));
		}
	}

	// === GETTERS ===

	public List<Player> getPlayers() {
		return players;
	}

	public int getNbPlayers() {
		return nbPlayers;
	}

	public int getMode() {
		return mode;
	}

	public boolean isExtensionActive() {
		return extensionActive;
	}

	public Card getTrophyCard1() {
		return trophyCard1;
	}

	public Card getTrophyCard2() {
		return trophyCard2;
	}

	public List<Card> getStack() {
		return stack;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public List<Offer> getOffers() {
		return offers;
	}
}
