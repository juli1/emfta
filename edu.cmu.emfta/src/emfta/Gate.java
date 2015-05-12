/**
 */
package emfta;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Gate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link emfta.Gate#getType <em>Type</em>}</li>
 *   <li>{@link emfta.Gate#getGates <em>Gates</em>}</li>
 *   <li>{@link emfta.Gate#getEvents <em>Events</em>}</li>
 * </ul>
 * </p>
 *
 * @see emfta.EmftaPackage#getGate()
 * @model
 * @generated
 */
public interface Gate extends EObject {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link emfta.GateType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see emfta.GateType
	 * @see #setType(GateType)
	 * @see emfta.EmftaPackage#getGate_Type()
	 * @model
	 * @generated
	 */
	GateType getType();

	/**
	 * Sets the value of the '{@link emfta.Gate#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see emfta.GateType
	 * @see #getType()
	 * @generated
	 */
	void setType(GateType value);

	/**
	 * Returns the value of the '<em><b>Gates</b></em>' containment reference list.
	 * The list contents are of type {@link emfta.Gate}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gates</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gates</em>' containment reference list.
	 * @see emfta.EmftaPackage#getGate_Gates()
	 * @model containment="true"
	 * @generated
	 */
	EList<Gate> getGates();

	/**
	 * Returns the value of the '<em><b>Events</b></em>' containment reference list.
	 * The list contents are of type {@link emfta.Event}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Events</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Events</em>' containment reference list.
	 * @see emfta.EmftaPackage#getGate_Events()
	 * @model containment="true"
	 * @generated
	 */
	EList<Event> getEvents();

} // Gate
