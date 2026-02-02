package model;

/**
 * Représente une carte individuelle dans le jeu de cartes Jest.
 * <p>
 * Chaque carte possède une valeur faciale et une couleur qui déterminent sa
 * priorité lors du tour de jeu ainsi que son poids dans le score final.
 * </p>
 * 
 * @author Mathéo A.
 * @author Jules CS
 * @see Color
 * @see model.TrophyCardResolver
 */
public class Card {

	private int value;
	private Color color;
	private boolean isVisible;
	private int priority;

	/**
	 * Construit une carte avec une valeur et une couleur données. La carte est
	 * initialement invisible et sa priorité est calculée automatiquement.
	 * 
	 * @param value la valeur numérique de la carte
	 * @param color la couleur de la carte
	 */
	public Card(int value, Color color) {
		this.value = value;
		this.color = color;
		this.isVisible = false;
		this.priority = computePriority(color);
	}

	/**
	 * Crée une copie profonde d'une carte existante.
	 * 
	 * @param card la carte à copier
	 */
	public Card(Card card) {
		this.value = card.getFaceValue();
		this.isVisible = card.isVisible();
		this.color = card.getColor();
		this.priority = card.getPriority();
	}

	/**
	 * Retourne la couleur de la carte.
	 * 
	 * @return la couleur de la carte
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Retourne la valeur numérique de la carte.
	 * 
	 * @return la valeur de la carte
	 */
	public int getFaceValue() {
		return value;
	}

	/**
	 * Indique si la carte est visible ou face cachée.
	 * 
	 * @return true si la carte est visible, false sinon
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Modifie l'état de visibilité de la carte.
	 * 
	 * @param v true pour rendre la carte visible, false pour la cacher
	 */
	public void setVisible(boolean v) {
		this.isVisible = v;
	}

	/**
	 * Calcule la priorité d'une carte selon les règles définies.
	 * <p>
	 * La priorité est utilisée pour définir l'ordre dans lequel les joueurs
	 * choisissent leurs cartes durant la phase de ramassage.
	 * </p>
	 * 
	 * @return La valeur entière de priorité (Somme de la base couleur et de la
	 *         valeur).
	 */
	public int getPriority() {
		return priority + value;
	}

	/**
	 * Calcule la priorité de base en fonction de la couleur. JOKER=100, HEART=4,
	 * SPADE=3, CLUB=2, DIAMOND=1.
	 * 
	 * @param c la couleur pour laquelle calculer la priorité
	 * @return la priorité de base de la couleur
	 */
	private int computePriority(Color c) {
		switch (c) {
		case JOKER:
			return 100;
		case HEART:
			return 4;
		case SPADE:
			return 3;
		case CLUB:
			return 2;
		case DIAMOND:
			return 1;
		default:
			return 0;
		}
	}

	@Override
	public String toString() {
		return color + " " + value;
	}

}
