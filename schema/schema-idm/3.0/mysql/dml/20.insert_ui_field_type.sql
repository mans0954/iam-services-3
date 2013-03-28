use openiam;

INSERT INTO METADATA_TYPE(TYPE_ID,DESCRIPTION, ACTIVE, SYNC_MANAGED_SYS, GROUPING)
            VALUES('TEXT', 'Simple text field','Y','N','UI_WIDGET'),
                  ('DATE', 'Field to enter date','Y','N','UI_WIDGET'),
                  ('SELECT', 'Single combobox','Y','N','UI_WIDGET'),
                  ('MULTI_SELECT', 'Multiselect list','Y','N','UI_WIDGET'),
                  ('RADIO', 'Radiobutton control','Y','N','UI_WIDGET'),
                  ('CHECKBOX', 'Checkbox control','Y','N','UI_WIDGET'),
                  ('TEXTAREA', 'Multiline text box','Y','N','UI_WIDGET'),
                  ('PASSWORD', 'Simple text field to enter password','Y','N','UI_WIDGET');