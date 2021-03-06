/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyadq.calculation.hiv;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyadq.DqMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link MissingHivConfirmedDateCalculation}
 */
@Ignore
public class MissingHivConfirmedDateCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("datasets/test-concepts.xml");
		executeDataSet("datasets/test-metadata.xml");

		Program hivProgram = MetadataUtils.getProgram(DqMetadata.Program.HIV);

		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2012, 5, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2012, 5, 1));

		Concept diagnosisDate = MetadataUtils.getConcept(DqMetadata.Concept.DATE_OF_HIV_DIAGNOSIS);

		TestUtils.saveObs(TestUtils.getPatient(6), diagnosisDate, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 5, 1));
	}

	/**
	 * @see MissingHivConfirmedDateCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate() {
		List<Integer> cohort = Arrays.asList(2, 6, 7);

		CalculationResultMap resultMap = new MissingHivConfirmedDateCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // Never enrolled in HIV
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // Has confirmed obs
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // Missing confirmed obs
	}
}