/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.contrib.qaframework.legacy;

import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openmrs.contrib.qaframework.helper.BuildTests;
import org.openmrs.contrib.qaframework.helper.TestBase;
import org.openmrs.contrib.qaframework.helper.TestData;
import org.openmrs.contrib.qaframework.helper.TestPatient;
import org.openmrs.contrib.qaframework.page.ClinicianFacingPatientDashboardPage;
import org.openmrs.contrib.qaframework.page.DataManagementPage;
import org.openmrs.contrib.qaframework.page.FindPatientPage;
import org.openmrs.contrib.qaframework.page.HomePage;
import org.openmrs.contrib.qaframework.page.RegistrationPage;

public class RecordMergeIssueTest extends TestBase {

    private HomePage homePage;
    private FindPatientPage findPatientPage;
    private TestPatient patient1;
    private TestPatient patient2;
    private RegistrationPage registrationPage;
    private ClinicianFacingPatientDashboardPage patientDashboardPage;
    private DataManagementPage dataManagementPage;
    private String id;
    private String id2;

    @Before
    public void setUp() throws Exception {
        homePage = new HomePage(page);
        assertPage(homePage.waitForPage());
        findPatientPage = new FindPatientPage(page);
        registrationPage = new RegistrationPage(page);
        patientDashboardPage = new ClinicianFacingPatientDashboardPage(page);
        dataManagementPage = new DataManagementPage(page);
        patient1 = new TestPatient();
        patient2 = new TestPatient();
    }

    @Test
    @Category(BuildTests.class)
    public void recordMergeIssueTest() throws Exception {
        homePage.goToRegisterPatientApp().waitForPage();
        // Register first patient
        patient1.familyName = "Mike";
        patient1.givenName = "Smith";
        patient1.gender = "Male";
        patient1.estimatedYears = "25";
        patient1.address1 = "address";
        registrationPage.enterMergePatient(patient1);
        id = patientDashboardPage.findPatientId();
        patient1.uuid = patientDashboardPage.getPatientUuidFromUrl();
        homePage.go();
        
        // Register second patient
        homePage.goToRegisterPatientApp();
        patient2.familyName = "Mike";
        patient2.givenName = "Kowalski";
        patient2.gender = "Male";
        patient2.estimatedYears = "25";
        patient2.address1 = "address";
        registrationPage.enterMergePatient(patient2);
        id2 = patientDashboardPage.findPatientId();
        homePage.go();
        
        // Merge patient data
        homePage.goToDataManagement();
        dataManagementPage.goToMergePatient();
        dataManagementPage.enterPatient1(id);
        dataManagementPage.enterPatient2(id2);
        dataManagementPage.clickOnContinue();
        assertFalse(driver.getPageSource().contains("java.lang.NullPointerException"));
    }

    @After
    public void tearDown() throws Exception {
        homePage.go();
        TestData.PatientInfo p = new TestData.PatientInfo();
        p.uuid = patient1.uuid;
        deletePatient(p);
        waitForPatientDeletion(patient1.uuid);
    }
}
