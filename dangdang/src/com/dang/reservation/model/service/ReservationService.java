package com.dang.reservation.model.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dang.common.code.ConfigCode;
import com.dang.common.exception.DataAccessException;
import com.dang.common.exception.ToAlertException;
import com.dang.common.jdbc.JDBCTemplate;
import com.dang.common.mail.MailSender;
import com.dang.common.util.http.HttpUtils;
import com.dang.member.user.model.vo.UserMember;
import com.dang.reservation.model.dao.ReservationDao;
import com.dang.reservation.model.vo.Reservation;

public class ReservationService {

	JDBCTemplate jdt = JDBCTemplate.getInstance();
	ReservationDao reservationDao = new ReservationDao();

	// 예약 신청 메서드
	public void insertReservation(Reservation reservation) {
		Connection conn = jdt.getConnection();

		try {

			reservationDao.insertReservation(conn, reservation);

			jdt.commit(conn);

		} catch (DataAccessException e) {

			jdt.rollback(conn);
			throw new ToAlertException(e.error, e);

		} finally {
			jdt.close(conn);
		}

	}

	// 유치원 예약 확인 페이지 페이징
	public List<Reservation> selectReservationPage(int startRow, int endRow, String kgName) {
		Connection conn = jdt.getConnection();
		List<Reservation> reservation;
		try {
			reservation = reservationDao.selectReservationPage(conn, startRow, endRow, kgName);

		} finally {
			jdt.close(conn);
		}
		return reservation;

	}

	// 유치원 예약 총 개수 메서드
	public int selectCountPage(String kgName) {
		Connection conn = jdt.getConnection();

		int count;
		try {
			count = reservationDao.selectCountPage(conn, kgName);
		} finally {
			jdt.close(conn);
		}
		return count;

	}

	// 유저 정보를 찾아주는 메서드
	public UserMember selectUserMember(String userId) {
		Connection conn = jdt.getConnection();

		UserMember userMember;
		try {
			userMember = reservationDao.selectUserMember(conn, userId);
		} finally {
			jdt.close(conn);
		}
		return userMember;

	}

	public void ReservationEmail(UserMember userMember, String date, String KgName) {

		String subject = "유치원 예약을 확인해주세요";
		String htmlText = "<h1>고객님이 신청하신 예약이 승인이 완료되었습니다.</h1>";
		HttpUtils http = new HttpUtils();
		Map<String, String> headers = new HashMap<String, String>();

		// 우리서버의 url
		String url = ConfigCode.DOMAIN + "/mail.do";

		// header 저장
		headers.put("Content-Type", "application/x-www-form-urlencoded");

		// 파라미터 저장
		Map<String, String> params = new HashMap<String, String>();

		params.put("template", "reservation");
		params.put("userId", userMember.getUserId());
		params.put("date", date);
		params.put("KgName", KgName);

		htmlText = http.post(url, http.urlEncodedForm(params), headers);

		// body에 넣어서 전송
		// 네이버 메일이면 네이버 메일 도메인 뒤로 붙여서 우리 도메인 뒤로 붙게해줘야함
		String to = userMember.getEmail();

		new MailSender().sendEmail(subject, htmlText, to);

	}

	// 예약 승인해주는 메서드
	public int updateReservation(String rsIdx) {
		Connection conn = jdt.getConnection();
		int res = 0;
		try {
			res = reservationDao.updateReservation(conn, rsIdx);
			jdt.commit(conn);
		} catch (DataAccessException e) {
			jdt.rollback(conn);
			throw new ToAlertException(e.error);
		} finally {
			jdt.close(conn);
		}
		return res;

	}

	// 캘린더 예약 내용 불러오기
	public ArrayList<Reservation> selectReservation(String kgName) {
		Connection conn = jdt.getConnection();
		ArrayList<Reservation> reservationList;
		try {
			reservationList = reservationDao.selectReservation(conn, kgName);

		} finally {
			jdt.close(conn);
		}
		return reservationList;

	}

	// 예약 삭제 메서드
	public int deleteReservation(String rsIdx) {
		Connection conn = jdt.getConnection();
		int res = 0;
		try {
			res = reservationDao.deleteReservation(conn, rsIdx);
			jdt.commit(conn);
		} catch (DataAccessException e) {
			jdt.rollback(conn);
			throw new ToAlertException(e.error);
		} finally {
			jdt.close(conn);
		}
		return res;

	}

	// 유치원 예약 미리보기
	public ArrayList<Reservation> selectReservationPreview(String kgName) {
		Connection conn = jdt.getConnection();
		ArrayList<Reservation> reservationList;
		try {
			reservationList = reservationDao.selectReservationPreview(conn, kgName);

		} finally {
			jdt.close(conn);
		}
		return reservationList;

	}

	// 유저 예약 미리보기
	public ArrayList<Reservation> selectUserPreview(String userId) {
		Connection conn = jdt.getConnection();
		ArrayList<Reservation> reservationList;
		try {
			reservationList = reservationDao.selectUserPreview(conn, userId);

		} finally {
			jdt.close(conn);
		}
		return reservationList;

	}

	// 유저 예약 확인 페이지 페이징
	public List<Reservation> selectReservationUserPage(int startRow, int endRow, String userId) {
		Connection conn = jdt.getConnection();
		List<Reservation> reservation;
		try {
			reservation = reservationDao.selectReservationUserPage(conn, startRow, endRow, userId);

		} finally {
			jdt.close(conn);
		}
		return reservation;

	}

	// 유치원 예약 총 개수 메서드
	public int selectCountPageUser(String userId) {
		Connection conn = jdt.getConnection();

		int count;
		try {
			count = reservationDao.selectCountPageUser(conn, userId);
		} finally {
			jdt.close(conn);
		}
		return count;

	}

}
