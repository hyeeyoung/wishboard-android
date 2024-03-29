# Wishboard
<img width="150" alt="app_logo" src="https://user-images.githubusercontent.com/48701368/157261515-b0809692-3fc6-46eb-b4db-a0849cd557b6.svg">

<br>

__위시리스트 통합관리 앱, 위시보드(Wishboard)__

Timeline: 21.9.26 - 22.3.8 (+ iOS 개발 예정)

TEAM: Hyeeyoung

<br>

## Contents
* [About Wishboard](#-About-Wishboard)
* [Contributors](#-Contributors)
* [Service Workflow](#-Service-Workflow)
* [Develop Environment](#-Develop-Environment)
* [Library & Technology Stack](#-Library-&-Technology-Stack)
* [Feature](#-Feature)
* [Directory Structure](#-Directory-Structure)
* [Meeting Note](#-Meeting-Note)
* [Convention](#-Convention)

<br>

## About Wishboard
<img width="1200" alt="thumbnail" src="https://user-images.githubusercontent.com/48701368/157662866-afc6bc95-4c7c-41d0-aab9-385aeb129cec.png">

화면 캡처나 페이지 즐겨찾기, 카톡 나에게 보내기는 이제 그만! Wishboard로 간편하게 위시리스트를 통합 관리해 보세요😉

여러 쇼핑몰 플랫폼에 흩어져있는 아이템들, 사고 싶은 아이템을 링크 공유로 Wishboard에 저장합니다. 저장 시 아이템의 재입고 날짜, 프리오더 날짜와 같이 상품 일정을 설정하면 알림을 보내줍니다. 

구매할 아이템은 장바구니에 담아서 최종 결제 금액을 예상해보고, 폴더로 아이템을 보기 쉽게 정리할 수 있습니다!

<br>

## Contributors

|<img src="https://user-images.githubusercontent.com/48701368/157186833-1f852f89-1094-4d92-ba3c-de5a706ed7e1.jpg" width="60%" /> | <img src="https://user-images.githubusercontent.com/68772751/139533613-e4695172-50b5-4f12-8d39-0dd93de7b774.png" width="60%" /> |
| --- | --- |
| [최영진](https://github.com/youngjinc) / Android & Design | [김혜정](https://github.com/hyejungg) / Server |

<br>

## User Flow

<img width="1200" alt="userflow" src="https://user-images.githubusercontent.com/48701368/157239273-7ded387e-4204-486f-ad4c-de0a61e18a60.png">

<br>

## Develop Environment

![kotlin](https://img.shields.io/static/v1?style=for-the-badge&logo=kotlin&message=Kotlin&label=&color=7F52FF&labelColor=000000)

<br>

## Library & Technology Stack

* **Dagger Hilt**
    * HiltViewModel로 View와 독립적으로 데이터 처리 로직을 분리하여 코드의 유지 보수를 쉽게하고, ViewModel로 프래그먼트 간 데이터를 쉽게 공유하기 위함
* **Navigation Component**
    * UI 전환을 쉽고 간편하게 구현하기 위함
* **Paging**
    * 대량의 갤러리 이미지 파일을 페이지 단위로 나누어 이미지를 효율적으로 로드하기 위함 (갤러리 이미지 뷰를 커스텀함)
* **DataBinding**
    * 레이아웃(xml)에 데이터를 연결(binding)하여 코드를 간략하게 작성하기 위함
* **Jsoup**
    * 링크 공유로 아이템 등록 시 쇼핑몰 페이지에서 상품명, 가격, 상품 이미지를 파싱하기 위함
* **Retrofit2**
    * REST API를 통해 서버와 통신하기 위함
* **Firebase FCM**
    * 사용자가 재입고, 프리오더와 같은 아이템 일정을 지정한 경우 해당 일자에 푸시알림을 수신하기 위함
* **AWS S3**
    * 아이템 이미지 및 프로필 이미지를 저장하기 위함
* **Lottie**
    * 회원가입 및 로그인, 아이템 업로드, 프로필 수정 뷰에서 로딩 에니메이션을 보여주기 위함

<br>

## Feature

| 분류 | 기능 | 진척도 |
| --- | --- | --- |
| `auth` | 회원가입 뷰 | ✔️ |
| `auth` | 로그인 뷰 | ✔️ |
| `auth` | 이메일 인증 | ✔️ |
| `item` | 홈 뷰 | ✔️ | 
| `item` | 아이템 디테일 뷰 | ✔️ | 
| `item` | 아이템 업로드 - 링크 공유 뷰 | ✔️ |
| `item` | 아이템 업로드 - 일반 뷰 | ✔️ |
| `item`, `my` | 커스텀 갤러리 뷰 | ✔️ |
| `search` | 검색 뷰 | 배포 이후 |
| `cart` | 장바구니 뷰 | ✔️ |
| `cart` | 장바구니 공유 뷰 | 배포 이후 |
| `folder` | 폴더 뷰 | ✔️ |
| `folder` | 폴더 디테일 뷰 | ✔️ |
| `noti` | 알림 뷰 | ✔️ |
| `noti` | 알림 설정 뷰 | ✔️ |
| `noti` | 푸시 알림 | ✔️ |
| `noti` | 캘린더 뷰 | 배포 이후 |
| `my` | 마이페이지 뷰 | ✔️ |
| `my` | 프로필 설정뷰 뷰 | ✔️ |

<br>

## Program Structure

```
📂com.hyeeyoung.wishboard
┣ 📂di
┃ ┣ 📜RepositoryModule.kt
┣ 📂model
┃ ┣ 📂cart
┃ ┃ ┣ 📜CartItem.kt
┃ ┃ ┣ 📜CartButtonType.kt
┃ ┃ ┣ 📜CartStateType.kt
┃ ┣ 📂common
┃ ┃ ┣ 📜DialogButtonReplyType.kt
┃ ┃ ┣ 📜ProcessStatus.kt
┃ ┣ 📂folder
┃ ┃ ┣ 📜FolderItem.kt
┃ ┃ ┣ 📜FolderListViewType.kt
┃ ┃ ┣ 📜FolderMoreDialogButtonReplyType.kt
┃ ┣ 📂noti
┃ ┃ ┣ 📜NotiItem.kt
┃ ┃ ┣ 📜NotiType.kt
┃ ┃ ┣ 📜ReadStateType.kt
┃ ┣ 📂user
┃ ┃ ┣ 📜UserInfo.kt
┃ ┣ 📂wish
┃ ┃ ┣ 📜WishItem.kt
┃ ┃ 📜RequestResult.kt
┃ ┃ 📜RequestResultData.kt
┣ 📂Service
┃ ┣ 📜AWSS3Service.kt
┃ ┣ 📜FireBaseMessagingService.kt
┃ ┣ 📜RemoteService.kt
┣ 📂repository
┃ ┣ 📂cart
┃ ┃ ┣ 📜CartRepository.kt
┃ ┃ ┣ 📜CartRepositoryImpl.kt
┃ ┣ 📂custom
┃ ┃ ┣ 📜GalleryPagingDataSource.kt
┃ ┃ ┣ 📜GalleryRepository.kt
┃ ┃ ┣ 📜GalleryRepositoryImpl.kt
┃ ┣ 📂folder
┃ ┃ ┣ 📜FolderRepository.kt
┃ ┃ ┣ 📜FolderRepositoryImpl.kt
┃ ┣ 📂noti
┃ ┃ ┣ 📜NotiRepository.kt
┃ ┃ ┣ 📜NotiRepositoryImpl.kt
┃ ┣ 📂sign
┃ ┃ ┣ 📜SignRepository.kt
┃ ┃ ┣ 📜SignRepositoryImpl.kt
┃ ┣ 📂user
┃ ┃ ┣ 📜UserRepository.kt
┃ ┃ ┣ 📜UserRepositoryImpl.kt
┃ ┣ 📂wish
┃ ┃ ┣ 📜WishRepository.kt
┃ ┃ ┣ 📜WishRepositoryImpl.kt
┣ 📂util
┃ ┣ 📂custom
┃ ┃ ┣ 📜CustomDecoration.kt
┃ ┣ 📂extension
┃ ┃ ┣ 📜NavController.kt
┃ ┣ 📜BindingAdapters.kt
┃ ┣ 📜DateFormatUtil.kt
┃ ┣ 📜DialogListener.kt
┃ ┣ 📜NetworkConnection.kt
┃ ┣ 📜NotificationUtil.kt
┃ ┣ 📜NumberPickerUtil.kt
┃ ┣ 📜PreferenceUtil.kt
┃ ┣ 📜TimeUtil.kt
┃ ┣ 📜Util.kt
┣ 📂view
┃ ┣ 📂cart
┃ ┃ ┣ 📂adapters
┃ ┃ ┃ ┣ 📜CartListAdapter.kt
┃ ┃ ┣ 📂screens
┃ ┃ ┃ ┣ 📜CartFragment.kt
┃ ┣ 📂common
┃ ┃ ┣ 📂adapters
┃ ┃ ┃ ┣ 📜GalleryPagingAdapter.kt
┃ ┃ ┣ 📂screens
┃ ┃ ┃ ┣ 📜GalleryImageFragment.kt
┃ ┃ ┃ ┣ 📜TwoButtonDialogFragment.kt
┃ ┣ 📂folder
┃ ┃ ┣ 📂adapters
┃ ┃ ┃ ┣ 📜FolderListAdapter.kt
┃ ┃ ┣ 📂screens
┃ ┃ ┃ ┣ 📜FolderAddDialogFragment.kt
┃ ┃ ┃ ┣ 📜FolderDetailFragment.kt
┃ ┃ ┃ ┣ 📜FolderFragment.kt
┃ ┃ ┃ ┣ 📜FolderListFragment.kt
┃ ┃ ┃ ┣ 📜FolderMoreDialogFragment.kt
┃ ┣ 📂my
┃ ┃ ┣ 📂screens
┃ ┃ ┃ ┣ 📜MyFragment.kt
┃ ┃ ┃ ┣ 📜MyProfileEditFragment.kt
┃ ┣ 📂noti
┃ ┃ ┣ 📂adapters
┃ ┃ ┃ ┣ 📜NotiListAdapter.kt
┃ ┃ ┣ 📂screens
┃ ┃ ┃ ┣ 📜NotiFragment.kt
┃ ┃ ┃ ┣ 📜NotiSettingFragment.kt
┃ ┣ 📂sign
┃ ┃ ┣ 📂screens
┃ ┃ ┃ ┣ 📜SignActivity.kt
┃ ┃ ┃ ┣ 📜SignFragment.kt
┃ ┃ ┃ ┣ 📜SignInEmailFragment.kt
┃ ┃ ┃ ┣ 📜SignInFragment.kt
┃ ┃ ┃ ┣ 📜SignUpEmailFragment.kt
┃ ┃ ┃ ┣ 📜SignUpPasswordFragment.kt
┃ ┣ 📂wish
┃ ┃ ┣ 📂item
┃ ┃ ┃ ┣ 📂screens
┃ ┃ ┃ ┃ ┣ 📜WishBasicFragment.kt
┃ ┃ ┃ ┃ ┣ 📜WishItemDetailFragment.kt
┃ ┃ ┃ ┃ ┣ 📜WishLinkSharingFragment.kt
┃ ┃ ┣ 📂list
┃ ┃ ┃ ┣ 📂adapters
┃ ┃ ┃ ┃ ┣ 📜WishListAdapter.kt
┃ ┃ ┃ ┣ 📂screens
┃ ┃ ┃ ┃ ┣ 📜HomeFragment.kt
┃ ┣ 📜MainActivity.kt
┃ ┣ 📜SplashActivity.kt
┣ 📂viewmodel
┃ ┣ 📜CartViewModel.kt
┃ ┣ 📜FolderViewModel.kt
┃ ┣ 📜GalleryViewModel.kt
┃ ┣ 📜MainViewModel.kt
┃ ┣ 📜NotiViewModel.kt
┃ ┣ 📜SignViewModel.kt
┃ ┣ 📜WishItemRegistrationViewModel.kt
┃ ┣ 📜WishItemViewModel.kt
┃ ┣ 📜WishListViewModel.kt
┣ 📜WishBoardApp.kt
```
<br>

## Meeting Note
[hyeeyoung의 10개월 간의 발자취예요! Click ✔](https://www.notion.so/84c305675a7e43308bc8c90e94afeb9c?v=d8fbe05719154feeb03b8c234a5b861f)

<br>

## Convention
[Git Convention 바로가기 Click ✔](https://www.notion.so/Git-fcda833780394a6bb658e1473d480d2f)

[Code Convention 바로가기 Click ✔](https://www.notion.so/Android-37031c99b0d84cd5b03aa2e9197b4e96)