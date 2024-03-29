package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.dal.MpaDal;

import java.util.Collection;

@Service
public class MpaServiceImpl implements MpaService {
    private final MpaDal mpaDao;
    @Autowired
    public MpaServiceImpl(MpaDal mpaDao) {
        this.mpaDao = mpaDao;
    }
    @Override
    @Transactional
    public Collection<Mpa> getMpa() {
        return mpaDao.getMpa();
    }

    @Override
    @Transactional
    public Mpa getMpaById(Long id) {
        checkMpaById(id);
        return mpaDao.getMpaById(id);
    }

    @Override
    @Transactional
    public String getMpaNameById(Long id) {
        checkMpaById(id);
        return mpaDao.getMpaNameById(id);
    }

    private void checkMpaById(Long id){
        try {
            mpaDao.getMpaById(id);
        }catch (EmptyResultDataAccessException e){
            throw new IllegalArgumentException("Рейтин с ID: " + id + " не найден!");
        }
    }
}
