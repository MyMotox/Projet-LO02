package model;

/**
 * Interface du pattern Visitor pour les objets visitables. Permet aux objets
 * d'accepter un visiteur pour effectuer des opérations comme le calcul de
 * score.
 */
public interface Visitable {

	/**
	 * Accepte un visiteur et lui permet d'effectuer une opération.
	 * 
	 * @param visitor le visiteur à accepter
	 * @return le résultat de l'opération du visiteur (par exemple un score)
	 */
	public int accept(Visitor visitor);

}
