package view;

import java.util.List;

import model.Offer;
import model.Player;

/**
 * Interface commune pour toutes les vues du jeu (Console, GUI). Définit les
 * méthodes d'interaction avec l'utilisateur.
 */
public interface GameViewInterface extends GameObserver {

	/**
	 * Demande au joueur de faire son offre. Le joueur doit choisir quelle carte
	 * cacher (1 ou 2).
	 * 
	 * @param player le joueur qui fait l'offre
	 * @return l'index de la configuration d'offre (1 ou 2)
	 */
	int askForOffer(Player player);

	/**
	 * Demande au joueur de choisir une cible parmi les offres disponibles.
	 * 
	 * @param player          le joueur qui choisit
	 * @param availableOffers les offres disponibles
	 * @return l'index de l'offre choisie (1-based)
	 */
	int askForTarget(Player player, List<Offer> availableOffers);

	/**
	 * Demande au joueur de choisir une carte (visible ou cachée) dans une offre.
	 * 
	 * @param player      le joueur qui choisit
	 * @param targetOffer l'offre de la cible
	 * @return 1 pour la carte visible, 2 pour la carte cachée
	 */
	int askForCard(Player player, Offer targetOffer);

	/**
	 * Affiche un message à l'utilisateur.
	 * 
	 * @param message le message à afficher
	 */
	void displayMessage(String message);

	/**
	 * Démarre l'interface utilisateur.
	 */
	void start();

	/**
	 * Ferme l'interface
	 */
	void close();
}
