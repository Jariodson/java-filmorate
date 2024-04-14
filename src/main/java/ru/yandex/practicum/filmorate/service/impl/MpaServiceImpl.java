package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@Transactional
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaDao;

    @Autowired
    public MpaServiceImpl(MpaStorage mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Override
    public Collection<Mpa> getMpas() {
        return mpaDao.getMpa();
    }

    @Override
    public Mpa getMpaById(Long id) {
        validateMpaId(id);
        return mpaDao.getMpaById(id);
    }

    @Override
    public void validateMpaId(Long id) {
        try {
            mpaDao.getMpaById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтин с ID: " + id + " не найден!");
        }
    }
}
