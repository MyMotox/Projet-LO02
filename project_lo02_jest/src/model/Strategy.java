package model;

import java.util.List;

/**
 * Interface définissant la stratégie de jeu d'un joueur. Implémentée par les
 * joueurs réels (humains) et virtuels (IA). Utilise le pattern Strategy pour
 * séparer le comportement du joueur de sa structure.
 */
public interface Strategy {

	/**
	 * Permet au joueur de choisir une carte dans une offre.
	 * 
	 * @param current le joueur qui fait le choix
	 * @param o       l'offre dans laquelle choisir
	 * @param stack   la pile de cartes disponibles (contexte)
	 * @return la carte choisie (visible ou cachée)
	 */
	public Card choose(Player current, Offer o, List<Card> stack);

	/**
	 * Permet au joueur de choisir la cible (l'offre d'un autre joueur) à piocher.
	 * 
	 * @param p         le joueur qui fait le choix
	 * @param available la liste des offres disponibles
	 * @return le joueur cible dont l'offre sera utilisée
	 */
	public Player chooseTarget(Player p, List<Offer> available);

	/**
	 * Crée une offre à partir des cartes du joueur.
	 * 
	 * @param i l'index de la configuration d'offre (1 ou 2)
	 * @param p le joueur qui fait l'offre
	 * @return l'offre créée avec une carte visible et une carte cachée
	 */
	public Offer performOffer(int i, Player p);

	/**
	 * Affiche la main du joueur (pour les stratégies qui nécessitent un affichage).
	 * 
	 * @param p le joueur dont afficher la main
	 */
	public void displayHand(Player p);

	/**
	 * Crée une offre pour le joueur (méthode principale).
	 * 
	 * @param p le joueur qui fait l'offre
	 * @return l'offre créée
	 */
	public Offer offer(Player p);

}
