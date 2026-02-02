package view;

import java.util.List;

import model.Card;
import model.Offer;
import model.Player;

/**
 * Interface Observer pour notifier les vues des changements d'état du jeu.
 * Toutes les vues (Console, GUI) doivent implémenter cette interface.
 */
public interface GameObserver {

	/**
	 * Notifie qu'une nouvelle partie a démarré.
	 * 
	 * @param nbPlayers       le nombre de joueurs
	 * @param mode            le mode de jeu
	 * @param extensionActive true si l'extension est activée
	 */
	void onGameStarted(int nbPlayers, int mode, boolean extensionActive);

	/**
	 * Notifie qu'un nouveau round commence.
	 * 
	 * @param roundNumber le numéro du round
	 */
	void onRoundStarted(int roundNumber);

	/**
	 * Notifie que les cartes ont été distribuées.
	 * 
	 * @param players la liste des joueurs avec leurs cartes
	 */
	void onCardsDistributed(List<Player> players);

	/**
	 * Notifie qu'un joueur doit faire son offre.
	 * 
	 * @param player le joueur qui doit faire son offre
	 */
	void onPlayerTurnToOffer(Player player);

	/**
	 * Notifie qu'une offre a été faite par un joueur.
	 * 
	 * @param player le joueur qui a fait l'offre
	 * @param offer  l'offre créée
	 */
	void onOfferMade(Player player, Offer offer);

	/**
	 * Notifie qu'un joueur doit choisir une cible.
	 * 
	 * @param player          le joueur qui doit choisir
	 * @param availableOffers les offres disponibles
	 */
	void onPlayerTurnToChoose(Player player, List<Offer> availableOffers);

	/**
	 * Notifie qu'une carte a été choisie.
	 * 
	 * @param chooser le joueur qui a choisi
	 * @param target  le joueur cible
	 * @param card    la carte choisie
	 */
	void onCardChosen(Player chooser, Player target, Card card);

	/**
	 * Notifie la fin d'un round
	 */
	void onRoundEnded(List<Player> players);

	/**
	 * Notifie la fin de la partie avec les scores
	 */
	void onGameEnded(Player winner, List<Player> players, int[] scores);

	/**
	 * Notifie l'affichage des cartes trophées
	 */
	void onTrophyCardsDisplayed(Card trophy1, Card trophy2);

	/**
	 * Notifie l'affichage du stack (mode JEST Clair)
	 */
	void onStackDisplayed(List<Card> stack);

	/**
	 * Notifie qu'une partie a été sauvegardée
	 */
	void onGameSaved(String saveName);

	/**
	 * Notifie qu'une partie a été chargée
	 */
	void onGameLoaded(String saveName);

	/**
	 * Notifie une erreur
	 */
	void onError(String errorMessage);
}
