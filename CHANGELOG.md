# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Contextual search hints ("Search lists", "Search items").
- Created `TopAppBarTitle` shared composable for consistent title styling.

### Changed
- Refactored testing infrastructure to use an in-memory database and improved Hilt dependency injection.
- Refactored `MyApplication` to `BaseApplication` to support custom test applications.
- Updated testing dependencies and improved UI test robustness.

### Fixed
- Limited Action Bar title to a single line with ellipsis to comply with Material Design 2 guidelines for standard app bars.
- Fix compiler warnings

## [1.9] - 2024-05-04

### Added
- Splash screen that adapts to the device theme.
- "System" theme selection option in the preferences screen (separate from global device theme).

### Fixed
- Theme selection bug fix.
- Code refactoring and UI screen improvements.

## [1.6]

### Added
- Button to quickly add an item to the list if it's not found while searching.
