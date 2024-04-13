package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.dao.MpaDal;

import java.util.Collection;

@Service
@Transactional
public class MpaServiceImpl implements MpaService {
    private final MpaDal mpaDao;

    @Autowired
    public MpaServiceImpl(MpaDal mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Override
    public Collection<Mpa> getMpas() {
        return mpaDao.getMpa();
    }

    @Override
    public Mpa getMpaById(Long id) {
        validate(id);
        return mpaDao.getMpaById(id);
    }

    private void validate(Long id) {
        try {
            mpaDao.getMpaById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Рейтин с ID: " + id + " не найден!");
        }
    }
}
