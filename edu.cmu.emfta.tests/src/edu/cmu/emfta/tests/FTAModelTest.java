/**
 */
package edu.cmu.emfta.tests;

import edu.cmu.emfta.EmftaFactory;
import edu.cmu.emfta.FTAModel;

import junit.framework.TestCase;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>FTA Model</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class FTAModelTest extends TestCase {

	/**
	 * The fixture for this FTA Model test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FTAModel fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(FTAModelTest.class);
	}

	/**
	 * Constructs a new FTA Model test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FTAModelTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this FTA Model test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(FTAModel fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this FTA Model test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FTAModel getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(EmftaFactory.eINSTANCE.createFTAModel());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //FTAModelTest
