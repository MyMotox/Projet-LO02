package model;

/**
 * Interface du pattern Visitor pour le calcul des scores. Permet de séparer la
 * logique de calcul de la structure des données.
 */
public interface Visitor {

	/**
	 * Visite un joueur pour calculer son score.
	 * 
	 * @param player le joueur à visiter
	 * @return le score calculé pour ce joueur
	 */
	public int visit(Player player);

}
