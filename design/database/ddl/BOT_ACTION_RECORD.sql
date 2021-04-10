/*
 * Copyright 2021 Kato Shinya.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
DROP TABLE IF EXISTS BOT_ACTION_RECORD;
CREATE TABLE IF NOT EXISTS BOT_ACTION_RECORD(
  ID INT AUTO_INCREMENT,
  RECORD_TYPE INT NOT NULL,
  COUNT INT NOT NULL,
  CREATED_BY VARCHAR(50) NOT NULL,
  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UPDATED_BY VARCHAR(50) NOT NULL,
  UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(ID, RECORD_TYPE)
);
