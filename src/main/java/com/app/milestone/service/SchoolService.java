package com.app.milestone.service;

import com.app.milestone.domain.FileDTO;
import com.app.milestone.domain.PasswordDTO;
import com.app.milestone.domain.SchoolDTO;
import com.app.milestone.domain.Search;
import com.app.milestone.entity.School;
import com.app.milestone.repository.FileRepository;
import com.app.milestone.repository.MoneyRepository;
import com.app.milestone.repository.PeopleRepository;
import com.app.milestone.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolService {
    private final SchoolRepository schoolRepository;
    private final PeopleRepository peopleRepository;
    private final MoneyRepository moneyRepository;
    private final FileRepository fileRepository;


    //    보육원 회원가입
    public Long createSchool(SchoolDTO schoolDTO) {
        School school = schoolDTO.toEntity();
        schoolDTO.setUserEmail(school.getUserEmail());
        schoolDTO.setUserPassword(Base64.getEncoder().encodeToString(school.getUserPassword().getBytes()));
        schoolDTO.setUserName(school.getUserName());
        schoolDTO.setSchoolName(school.getSchoolName());
        schoolDTO.setSchoolZipcode(school.getAddress().getSchoolZipcode());
        schoolDTO.setSchoolAddress(school.getAddress().getSchoolAddress());
        schoolDTO.setSchoolAddressDetail(school.getAddress().getSchoolAddressDetail());
        schoolDTO.setSchoolTeachers(school.getSchoolTeachers());
        schoolDTO.setSchoolKids(school.getSchoolKids());
        schoolDTO.setSchoolBudget(school.getSchoolBudget());
        schoolDTO.setSchoolBank(school.getSchoolBank());
        schoolDTO.setSchoolAccount(school.getSchoolAccount());
        schoolDTO.setUserPhoneNumber(school.getUserPhoneNumber());
        schoolDTO.setSchoolPhoneNumber(school.getSchoolPhoneNumber());
        schoolRepository.save(schoolDTO.toEntity());
        return school.getUserId();
    }

    //====================황지수====================
    //    보육원 등록
    //  보육원 등록시 QR코드를 생성하여 저장한다.
    @Transactional
    public void registerSchool(Long userId, SchoolDTO schoolDTO) {
        schoolDTO.setSchoolQR("https://chart.googleapis.com/chart?cht=qr&chs=200x200&chl=http://192.168.181.13:9999/login/login?prevSchoolId=" + userId);
        schoolRepository.findById(userId).get().update(schoolDTO);
    }

    //====================황지수====================
    //    도움이 필요한 보육원(메인)
    //  도움이 필요한 보육원을 조회하고 지역이름을 잘라서 돌려줍니다.
    public List<SchoolDTO> needHelpList() {
        List<SchoolDTO> arSchoolDTO = schoolRepository.findAllByDonationCount();
        for (SchoolDTO schoolDTO : arSchoolDTO) {
            schoolDTO.setSchoolAddress(schoolDTO.getSchoolAddress().split(schoolDTO.getSchoolAddress().charAt(2) + "")[0]);
        }
        return arSchoolDTO;
    }

    //====================황지수====================
    //    보육원 정보(하나)
    //  보육원정보와 보육원에 등록된 사진을 같이 돌려줍니다.
    public SchoolDTO schoolInfo(Long userId) {
        SchoolDTO schoolDTO = schoolRepository.findByUserId(userId);
        List<FileDTO> files = fileRepository.findByUserId(userId);
        schoolDTO.setFiles(files);
        return schoolDTO;
    }

    //====================황지수====================
    //    보육원 목록(보육원 목록)
    //  페이지에 대한 정보와 검색 조건을 받아와 페이징 처리후 돌려준다.
    public Page<SchoolDTO> schoolList(Integer page, Search search) {
        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        if (search.getSchoolAddress() == null) {
            search.setSchoolAddress(new ArrayList<>());
            search.getSchoolAddress().add(null);
        }
        if (search.getSchoolName() == null) {
            search.setSchoolName(null);
        }

        List<SchoolDTO> list = schoolRepository.findAllByCreatedDate(pageable, search);

        for (SchoolDTO schoolDTO : list) {
            List<FileDTO> arFileDTO = fileRepository.findByUserId(schoolDTO.getUserId());
            schoolDTO.setFiles(arFileDTO);
        }

        Page<SchoolDTO> schools = new PageImpl<>(list, pageable, Integer.valueOf("" + schoolRepository.countByCreatedDate(pageable, search)));

        return schools;
    }

    // 관리자 페이지============================================

    public List<School> schoolListManager(Pageable pageable) {
        return schoolRepository.findByCreatedDate(pageable);
    }

    public Page<SchoolDTO> schoolListSearch(Integer page, String keyword) {
        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 7);
        if (keyword == null) {
            keyword = null;
        }

        List<SchoolDTO> list = schoolRepository.findSchoolSearch(pageable, keyword);

        for (SchoolDTO schoolDTO : list) {
            FileDTO arFileDTO = fileRepository.findProfileByUserId(schoolDTO.getUserId());
            schoolDTO.setUserProfile(arFileDTO);
        }

        Page<SchoolDTO> school = new PageImpl<>(list, pageable, Integer.valueOf("" + schoolRepository.countByCreatedDate2(pageable, keyword)));
        return school;
    }

    public Page<SchoolDTO> schoolListSearchAsc(Integer page, String keyword) {
        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 7);
        if (keyword == null) {
            keyword = null;
        }
        List<SchoolDTO> list = schoolRepository.findSchoolSearchAsc(pageable, keyword);

        for (SchoolDTO schoolDTO : list) {
            FileDTO arFileDTO = fileRepository.findProfileByUserId(schoolDTO.getUserId());
            schoolDTO.setUserProfile(arFileDTO);
        }

        Page<SchoolDTO> school = new PageImpl<>(list, pageable, Integer.valueOf("" + schoolRepository.countByCreatedDate2(pageable, keyword)));
        return school;
    }

    //    보육원 검색 + 예산 내림차순
    public Page<SchoolDTO> schoolListBudgetSearch(Integer page, String keyword) {
        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 7);
        if (keyword == null) {
            keyword = null;
        }
        List<SchoolDTO> list = schoolRepository.findByBudgetAndSearch(pageable, keyword);


        for (SchoolDTO schoolDTO : list) {
            FileDTO arFileDTO = fileRepository.findProfileByUserId(schoolDTO.getUserId());
            schoolDTO.setUserProfile(arFileDTO);
        }

        Page<SchoolDTO> schools = new PageImpl<>(list, pageable, Integer.valueOf("" + schoolRepository.countByCreatedDate3(pageable, keyword)));
        return schools;
    }

    //    보육원 검색 + 예산 오름차순
    public Page<SchoolDTO> schoolListBudgetSearchAsc(Integer page, String keyword) {
        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 7);
        if (keyword == null) {
            keyword = null;
        }
        List<SchoolDTO> list = schoolRepository.findByBudgetAndSearchAsc(pageable, keyword);


        for (SchoolDTO schoolDTO : list) {
            FileDTO arFileDTO = fileRepository.findProfileByUserId(schoolDTO.getUserId());
            schoolDTO.setUserProfile(arFileDTO);
        }

        int start = list.size() >= (int) pageable.getOffset() ? (int) pageable.getOffset() : (int) pageable.getOffset() - 10;
        int end = Math.min((start + pageable.getPageSize()), list.size());

        Page<SchoolDTO> schools = new PageImpl<>(list.subList(start, end), pageable, Integer.valueOf("" + schoolRepository.countByCreatedDate3(pageable, keyword)));

        return schools;
    }

    //    회원 삭제
    public void deleteByUserId(Long userId) {
        schoolRepository.deleteById(userId);
    }

    //   재능기부 보육원 로그인 => 신청하기 school 아이디 저장
    public School selectSchoolId(Long userId) {
        return schoolRepository.findById(userId).get();
    }
    //  비밀번호 수정
    @Transactional
    public void updateSchoolPassword(Long userId, PasswordDTO passwordDTO) {
        String changePassword = Base64.getEncoder().encodeToString(passwordDTO.getChangePassword().getBytes());
        passwordDTO.setChangePassword(changePassword);
        log.info("피플 서비스 유저 비밀번호 : " + changePassword);
        log.info("피플 서비스 유저 아이디 : " + userId);
        schoolRepository.findById(userId).get().updatePassword(passwordDTO);
    }
}
