use openiam;

INSERT INTO METADATA_TYPE(TYPE_ID,DESCRIPTION, ACTIVE, SYNC_MANAGED_SYS, GROUPING)
            VALUES('TEXT', 'Text field','Y','N','UI_WIDGET'),
                  ('DATE', 'Date field','Y','N','UI_WIDGET'),
                  ('SELECT', 'Combo box','Y','N','UI_WIDGET'),
                  ('MULTI_SELECT', 'MultiSelect list','Y','N','UI_WIDGET'),
                  ('RADIO', 'Radio button','Y','N','UI_WIDGET'),
                  ('CHECKBOX', 'Checkbox','Y','N','UI_WIDGET'),
                  ('TEXTAREA', 'Multiline text field','Y','N','UI_WIDGET'),
                  ('PASSWORD', 'Password field','Y','N','UI_WIDGET');