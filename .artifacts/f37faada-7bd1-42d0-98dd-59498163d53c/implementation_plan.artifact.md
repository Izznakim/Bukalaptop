# Implementation Plan - Fix SignInPegawaiActivity Compose Preview

The Compose Previews for `SignInPegawaiActivity` are failing because they attempt to instantiate `SignInPegawaiViewModel` using the default `viewModel()` factory. The `ViewModel` has dependencies (`AuthRepository`) and no no-argument constructor, which causes a `RuntimeException` in the Preview environment.

This plan refactors the `SignInPegawaiScreen` Composable to be "stateless" by removing the direct dependency on the `ViewModel`, allowing it to be rendered in Previews with mock data.

## Proposed Changes

### [app](file:///X:/Project Android/Bukalaptop/app)

#### [MODIFY] [SignInPegawaiActivity.kt](file:///X:/Project Android/Bukalaptop/app/src/main/java/com/example/bukalaptop/pegawai/auth/presentation/SignInPegawaiActivity.kt)

- Refactor `SignInPegawaiScreen` to remove the `viewModel` parameter.
- Add `isEmailValid` and `isPasswordValid` parameters to `SignInPegawaiScreen`.
- Update `SignInPegawaiRoute` to pass the necessary state and events from the `ViewModel` to `SignInPegawaiScreen`.
- Fix the password field logic in `SignInPegawaiScreen` to correctly use `TextFieldState` and trigger `onPasswordChange`.
- Update `SignInPegawaiPreview` and `SignInPegawaiErrorPreview` to pass static values instead of using `viewModel()`.

## Verification Plan

### Automated Tests
- Run Compose Preview for `SignInPegawaiPreview` and `SignInPegawaiErrorPreview` in Android Studio.

### Manual Verification
- Verify that the `SignInPegawaiActivity` still works correctly when running the app (sign-in flow).
