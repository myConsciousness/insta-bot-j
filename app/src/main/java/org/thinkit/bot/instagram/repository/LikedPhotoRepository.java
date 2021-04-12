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

package org.thinkit.bot.instagram.repository;

import java.util.Collection;
import java.util.Optional;

import org.thinkit.bot.instagram.repository.entity.LikedPhoto;

import lombok.NonNull;

public interface LikedPhotoRepository {

    public Optional<LikedPhoto> findById(int id);

    public Optional<LikedPhoto> findByUserName(@NonNull String userName);

    public Collection<LikedPhoto> findAll();

    public long count(LikedPhoto likedPhoto);

    public void insert(LikedPhoto likedPhoto);

    public boolean update(LikedPhoto likedPhoto);

    public void delete(LikedPhoto likedPhoto);
}
