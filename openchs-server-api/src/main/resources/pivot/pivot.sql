SELECT
  individual.first_name,
  individual.last_name,
  g.name,
  a.title,
  c2.name catchment,
  programEncounter.encounter_date_time,
  op.name "Program",
  oet.name "Encounter",
  ${individual},
  ${programEnrolment},
  ${programEncounter}
FROM program_encounter programEncounter
  LEFT OUTER JOIN operational_encounter_type oet on programEncounter.encounter_type_id = oet.encounter_type_id
  LEFT OUTER JOIN program_enrolment programEnrolment ON programEncounter.program_enrolment_id = programEnrolment.id
  LEFT OUTER JOIN operational_program op ON op.program_id = programEnrolment.program_id
  LEFT OUTER JOIN individual individual ON programEnrolment.individual_id = individual.id
  LEFT OUTER JOIN gender g ON g.id = individual.gender_id
  LEFT OUTER JOIN address_level a ON individual.address_id = a.id
  LEFT OUTER JOIN catchment_address_mapping m2 ON a.id = m2.addresslevel_id
  LEFT OUTER JOIN catchment c2 ON m2.catchment_id = c2.id
WHERE op.uuid = '${operationalProgramUuid}'
  AND oet.uuid = '${operationalEncounterTypeUuid}'
  AND programEncounter.encounter_date_time IS NOT NULL
  AND programEnrolment.enrolment_date_time IS NOT NULL;