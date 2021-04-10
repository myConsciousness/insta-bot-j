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
INSERT INTO
  CAT_ACTION_STATUS (
    STATUS,
    STATUS_NAME,
    CREATED_BY,
    CREATED_AT,
    UPDATED_BY,
    UPDATED_AT
  )
VALUES
  (
    0,
    'PREPARING',
    'INSTA_BOT',
    NOW(),
    'INSTA_BOT',
    NOW()
  );
INSERT INTO
  CAT_ACTION_STATUS (
    STATUS,
    STATUS_NAME,
    CREATED_BY,
    CREATED_AT,
    UPDATED_BY,
    UPDATED_AT
  )
VALUES
  (
    1,
    'RUNNING',
    'INSTA_BOT',
    NOW(),
    'INSTA_BOT',
    NOW()
  );
INSERT INTO
  CAT_ACTION_STATUS (
    STATUS,
    STATUS_NAME,
    CREATED_BY,
    CREATED_AT,
    UPDATED_BY,
    UPDATED_AT
  )
VALUES
  (
    2,
    'INTERRUPTED',
    'INSTA_BOT',
    NOW(),
    'INSTA_BOT',
    NOW()
  );
INSERT INTO
  CAT_ACTION_STATUS (
    STATUS,
    STATUS_NAME,
    CREATED_BY,
    CREATED_AT,
    UPDATED_BY,
    UPDATED_AT
  )
VALUES
  (
    3,
    'COMPLETED',
    'INSTA_BOT',
    NOW(),
    'INSTA_BOT',
    NOW()
  );
