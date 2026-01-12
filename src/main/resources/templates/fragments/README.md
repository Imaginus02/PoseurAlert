# Fragments Thymeleaf - Documentation

## 📋 Vue d'ensemble

Ce dossier contient les **fragments HTML réutilisables** de l'application. Les fragments permettent de définir un élément HTML une seule fois et de le réutiliser dans plusieurs pages sans duplication de code.

## 🎯 Comment ça fonctionne ?

### Syntaxe Thymeleaf

```html
<div th:replace="fragments/navbar :: navbar()"></div>
```

**Explication :**
- `th:replace` : remplace la div par le contenu du fragment
- `fragments/navbar` : chemin du fichier (`src/main/resources/templates/fragments/navbar.html`)
- `:: navbar` : nom du fragment défini avec `th:fragment="navbar()"`
- `()` : paramètres passés au fragment

---

## 📦 Fragments disponibles

### navbar.html

La **navbar réutilisable** de l'application.

#### Caractéristiques
- ✅ Responsive (Bootstrap)
- ✅ Intégration Spring Security (`sec:authorize`)
- ✅ Navigation dynamique avec classe `active`
- ✅ Styles centralisés

#### Définition du fragment
```html
<nav class="navbar navbar-expand-lg navbar-dark" th:fragment="navbar(activePage='')">
```

#### Paramètres
| Paramètre | Type | Valeurs possibles | Description |
|-----------|------|-------------------|-------------|
| `activePage` | String | `appointments`, `profile`, `login`, `inscription`, `''` | Met la classe CSS `active` sur le lien correspondant |

#### Utilisation

**Sans page active :**
```html
<div th:replace="fragments/navbar :: navbar('')"></div>
```

**Avec page active (ex: Page des rendez-vous) :**
```html
<div th:replace="fragments/navbar :: navbar('appointments')"></div>
```

**Exemple complet dans index.html :**
```html
<body>
    <!-- Navigation -->
    <div th:replace="fragments/navbar :: navbar('')"></div>

    <!-- Main Content -->
    <main>
        ...
    </main>
</body>
```

---

## ✨ Avantages du système

1. **DRY (Don't Repeat Yourself)** : Pas de duplication de code
2. **Maintenance centralisée** : Modifier la navbar dans un seul fichier
3. **Cohérence** : Tous les styles et comportements sont identiques partout
4. **Performance** : Réduction de la taille du code HTML
5. **Dynamique** : Paramètres Thymeleaf pour adapter le contenu

---

## 🔧 Comment ajouter un nouveau fragment

### 1. Créer le fichier
```bash
# Créer fragments/moncomposant.html
```

### 2. Définir le fragment
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <style>
        /* Vos styles spécifiques */
    </style>
</head>
<body>
    <div th:fragment="moncomposant(param1='valeur')">
        <!-- Contenu du fragment -->
    </div>
</body>
</html>
```

### 3. L'utiliser dans vos pages
```html
<div th:replace="fragments/moncomposant :: moncomposant('valeur')"></div>
```

---

## 📝 Notes importantes

- Les fragments doivent avoir un document HTML valide (avec `<html>`, `<head>`, `<body>`)
- Les styles dans `<head>` seront intégrés lors du remplacement
- Thymeleaf supporte les conditions : `th:if`, `th:unless`, `sec:authorize`
- Les variables du modèle (Model) sont accessibles dans les fragments

---

## 🔗 Ressources
- [Documentation Thymeleaf - Fragments](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#template-layout)
- [Spring Security Thymeleaf](https://www.thymeleaf.org/doc/articles/springsecurity.html)
- [Bootstrap Documentation](https://getbootstrap.com/docs/5.3/getting-started/introduction/)
