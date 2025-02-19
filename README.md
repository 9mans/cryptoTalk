<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
</head>
<body>
  <h1>크립토톡(CryptoTalk)</h1>

  <!-- 1. 프로젝트 개요 -->
  <h2>1. 프로젝트 개요</h2>
  <ul>
    <li><strong>프로젝트 명:</strong> 크립토톡(CryptoTalk)</li>
    <li><strong>개발 인원:</strong> 1명</li>
    <li><strong>개발 기간:</strong> 2024년 12월 09일 ~ 2025년 01월 10일</li>
    <li><strong>주요 기능</strong>
      <ul>
        <li>카카오 Oauth2.0 로그인</li>
        <li>가상화폐 가격 알림 서비스
          <ul>
            <li>가상화폐 지정 및 알림 가격 설정</li>
            <li>실시간(또는 주기적) 가격 확인 후 알림 발송(카카오톡 메시지 활용)</li>
          </ul>
        </li>
      </ul>
    </li>
  </ul>

  <!-- 2. 개발 환경 -->
  <h2>2. 개발 환경</h2>
  <ul>
    <li><strong>프론트엔드:</strong>
      <ul>
        <li>Thymeleaf</li>
        <li>Bootstrap</li>
      </ul>
    </li>
    <li><strong>백엔드:</strong>
      <ul>
        <li>Java 17</li>
        <li>Spring Boot 3.3.6</li>
      </ul>
    </li>
    <li><strong>데이터베이스:</strong> MySQL 8.0</li>
    <li><strong>빌드 도구:</strong> Gradle</li>
    <li><strong>형상관리:</strong> Git </li>
  </ul>

  <!-- 3. 요구사항 -->
  <h2>3. 요구사항</h2>
  <h3>3.1 가상화폐 알림 기능</h3>
  <ol>
    <li><strong>가상화폐 알림 전송 등록</strong>
      <ul>
        <li>A. 가상화폐를 지정하고, 알림을 받을 희망하는 가격대 입력</li>
        <li>B. 알림 가격 설정 완료 시, 가격 도달 시점에 카카오톡으로 알림 전송</li>
      </ul>
    </li>
  </ol>

  <!-- 4. 기술적 고민 및 해결 -->
  <h2>4. 기술적 고민 및 해결</h2>
  
  <!-- 4.1 가격 정보 관리 -->
  <h3>4.1 가격 정보 관리</h3>
  <p><strong>고민:</strong> 외부 API(예: 업비트)를 매번 실시간 호출할지, 혹은 일정 주기로 DB에 저장할지 결정이 필요.  
  실시간 API 호출 시 데이터는 최신이지만, 트래픽 증가 시 API Rate Limit에 걸릴 우려가 있음</p>
  
  <p><strong>해결:</strong></p>
  <ul>
    <li>주기적으로 데이터를 수집하여 DB에 저장하고, 서비스 로직은 DB 조회를 통해 시세를 가져옴</li>
    <li>데이터가 100% 실시간은 아니지만, 이력 관리와 API Rate Limit 완화를 동시에 달성</li>
    <li>모든 마켓 정보를 한 번에 가져오면 응답 데이터가 커서 <code>DataBufferLimitException</code>이 발생</li>
    <li>원화 마켓만 우선 가져오도록 수정하여 문제 해결</li>
  </ul>

  <!-- 4.2 개인정보 암호화 -->
  <h3>4.2 개인정보 암호화</h3>
  <p><strong>고민:</strong></p>
  <ul>
    <li>비밀번호는 해시(단방향) 암호화를 사용</li>
    <li>개인정보(예: 전화번호)는 복호화가 가능한 양방향 암호화 필요 → AES 알고리즘 사용</li>
  </ul>
  <p><strong>문제:</strong> AES 암호화 키를 하드코딩하면 보안 취약점 발생.  
  키를 애플리케이션 실행 시마다 임의 생성하면, 이전에 암호화된 데이터 복호화 불가</p>

  <p><strong>해결:</strong></p>
  <ul>
    <li>로컬 환경: 키를 환경 변수에 저장 후 애플리케이션 시작 시 로드</li>
    <li>배포(클라우드) 환경: AWS Secrets Manager 등을 통해 키를 안전하게 관리</li>
  </ul>

  <!-- 4.3 카카오 OAuth2.0 무한 리디렉션 문제 -->
  <h3>4.3 카카오 OAuth2.0 무한 리디렉션 문제</h3>
  <p><strong>문제:</strong> Spring Security와 카카오 OAuth2 연동 과정에서 무한 리디렉션 발생.  
  인증 완료 후 <code>redirect_uri</code> 형식이 표준과 맞지 않아 인증이 실패하고 재시도 → 무한 루프</p>

  <p><strong>해결:</strong> Spring Security에서 요구하는 <code>redirect_uri</code> 형식을 준수하도록 수정.  
  공식 문서를 참고하여 표준 인증 흐름을 따르도록 재설정 → 문제 해결</p>

</body>
</html>
