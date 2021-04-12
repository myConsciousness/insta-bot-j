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

package org.thinkit.bot.instagram.repository.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import org.thinkit.bot.instagram.repository.LikedPhotoRepository;
import org.thinkit.bot.instagram.repository.entity.LikedPhoto;

import lombok.NonNull;

public final class LikedPhotoService implements LikedPhotoRepository, Serializable {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 1355789517089445030L;

    /**
     * The liked photo repository
     */
    private LikedPhotoRepository likedPhotoRepository;

    @Override
    public Optional<LikedPhoto> findById(int id) {
        return this.likedPhotoRepository.findById(id);
    }

    @Override
    public Optional<LikedPhoto> findByUserName(@NonNull String userName) {
        return this.likedPhotoRepository.findByUserName(userName);
    }

    @Override
    public Collection<LikedPhoto> findAll() {
        return this.likedPhotoRepository.findAll();
    }

    @Override
    public long count(LikedPhoto likedPhoto) {
        return this.likedPhotoRepository.count(likedPhoto);
    }

    @Override
    public void insert(LikedPhoto likedPhoto) {
        this.likedPhotoRepository.insert(likedPhoto);
    }

    @Override
    public boolean update(LikedPhoto likedPhoto) {
        return this.likedPhotoRepository.update(likedPhoto);
    }

    @Override
    public void delete(LikedPhoto likedPhoto) {
        this.likedPhotoRepository.delete(likedPhoto);
    }
}
