CREATE TABLE LOCATION_LOCATION_MAPPING (
  ID                 SERIAL PRIMARY KEY,
  LOCATION_ID        BIGINT,
  PARENT_LOCATION_ID BIGINT,
  VERSION            INTEGER,
  AUDIT_ID           BIGINT,
  UUID               VARCHAR(255),
  IS_VOIDED          BOOLEAN NOT NULL DEFAULT FALSE,
  UUID_REF           VARCHAR(255),
  ORGANISATION_ID    BIGINT
);

INSERT INTO LOCATION_LOCATION_MAPPING (UUID_REF, LOCATION_ID, PARENT_LOCATION_ID, VERSION, ORGANISATION_ID)
SELECT UUID, ID, PARENT_ID, VERSION, ORGANISATION_ID
FROM ADDRESS_LEVEL
WHERE PARENT_ID IS NOT NULL;

INSERT INTO AUDIT (UUID, CREATED_BY_ID, LAST_MODIFIED_BY_ID, CREATED_DATE_TIME, LAST_MODIFIED_DATE_TIME)
SELECT UUID, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM ADDRESS_LEVEL
WHERE PARENT_ID IS NOT NULL;

UPDATE LOCATION_LOCATION_MAPPING
SET AUDIT_ID = (SELECT ID
                FROM AUDIT
                WHERE AUDIT.UUID = LOCATION_LOCATION_MAPPING.UUID_REF);

UPDATE LOCATION_LOCATION_MAPPING SET UUID = MD5(RANDOM()::TEXT || CLOCK_TIMESTAMP()::TEXT)::UUID WHERE UUID IS NULL;

ALTER TABLE ONLY LOCATION_LOCATION_MAPPING
  ADD CONSTRAINT LOCATION_LOCATION_MAPPING_LOCATION FOREIGN KEY (LOCATION_ID) REFERENCES ADDRESS_LEVEL (ID),
  ADD CONSTRAINT LOCATION_LOCATION_MAPPING_PARENT_LOCATION FOREIGN KEY (PARENT_LOCATION_ID) REFERENCES ADDRESS_LEVEL (ID),
  ADD CONSTRAINT LOCATION_LOCATION_MAPPING_AUDIT FOREIGN KEY (AUDIT_ID) REFERENCES AUDIT (ID),
  ADD CONSTRAINT LOCATION_LOCATION_MAPPING_ORGANISATION FOREIGN KEY (ORGANISATION_ID) REFERENCES ORGANISATION (ID),
  ALTER COLUMN VERSION SET NOT NULL,
  ALTER COLUMN UUID SET NOT NULL,
  DROP COLUMN UUID_REF;

ALTER TABLE ONLY ADDRESS_LEVEL
  DROP COLUMN PARENT_ID;