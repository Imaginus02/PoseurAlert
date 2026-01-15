-- =====================================================
-- TEST DATA FOR POSEUR ALERT APPLICATION
-- =====================================================

-- =====================================================
-- 1. USERS - Multiple professionals with different sectors
-- =====================================================
INSERT INTO sp_user (email, password, company_name, sector, address, phone_number, siret, description, business_hours, is_public_profile) VALUES
-- Restaurant
('alice@restaurant.com', '{noop}password123', 'Le Petit Bistro', 'RESTAURANT', '123 Rue de Paris, 75001 Paris', '0123456789', '12345678901234', 'Bistro français traditionnel avec cuisine maison', 'Lun-Ven: 11h-23h, Sam-Dim: 12h-23h', true),

-- Health Professionals
('dr.martin@clinic.com', '{noop}password123', 'Cabinet Dr. Martin', 'HEALTH_PROFESSIONAL', '456 Avenue des Champs, 75008 Paris', '0234567890', '23456789012345', 'Cabinet de médecine générale', 'Lun-Ven: 9h-18h, Sam: 9h-13h', true),
('dentist@smile.com', '{noop}password123', 'Clinique du Sourire', 'HEALTH_PROFESSIONAL', '789 Boulevard St-Michel, 75005 Paris', '0345678901', '34567890123456', 'Clinique dentaire moderne avec équipements dernier cri', 'Lun-Ven: 8h-19h, Sam: 9h-14h', true),

-- Garage
('meunier@garage.com', '{noop}password123', 'Garage Meunier', 'GARAGE', '234 Route de Lyon, 69000 Lyon', '0456789012', '45678901234567', 'Garage automobile réparation et entretien', 'Lun-Ven: 8h-18h, Sam: 9h-12h', false),

-- Hairdresser
('coiffure@mode.com', '{noop}password123', 'Coiffure Mode', 'HAIRDRESSER', '567 Rue du Faubourg, 13000 Marseille', '0567890123', '56789012345678', 'Salon de coiffure mixte avec services de colorations', 'Lun: Fermé, Mar-Sam: 9h-18h, Dim: 10h-17h', true),

-- Beauty Institute
('beaute@institut.com', '{noop}password123', 'Institut de Beauté Luxe', 'BEAUTY_INSTITUTE', '890 Avenue Montaigne, 75008 Paris', '0678901234', '67890123456789', 'Institut de beauté haut de gamme: soins, manucure, massage', 'Lun-Dim: 10h-20h', true),

-- Veterinarian
('vet@animaux.com', '{noop}password123', 'Clinique Vétérinaire Léon', 'VETERINARIAN', '321 Boulevard de la Paix, 92100 Boulogne', '0789012345', '78901234567890', 'Clinique vétérinaire équipée avec urgences 24h/24', 'Lun-Ven: 9h-19h, Sam: 10h-13h, Dim: 15h-18h (urgences)', true),

-- Additional test user for appointment testing
('bob@salon.com', '{noop}password123', 'Salon Bob', 'HAIRDRESSER', '111 Rue Test, 75002 Paris', '0111111111', '99999999999999', 'Petit salon de quartier', 'Lun-Ven: 9h-18h', false);

-- =====================================================
-- 2. CLIENT PHONES - Tracked phone numbers
-- =====================================================
INSERT INTO sp_client_phone (id, phone_number, report_count, last_report_date, is_flagged, created_at, updated_at) VALUES
-- Clean numbers (no reports)
(1, '0123456789', 0, NULL, false, NOW(), NOW()),
(2, '0987654321', 0, NULL, false, NOW(), NOW()),

-- Slightly problematic (1-2 reports)
(3, '0612345678', 1, NOW() - INTERVAL '5' DAY, false, NOW() - INTERVAL '10' DAY, NOW() - INTERVAL '5' DAY),
(4, '0645678901', 2, NOW() - INTERVAL '2' DAY, true, NOW() - INTERVAL '15' DAY, NOW() - INTERVAL '2' DAY),

-- Problematic (3+ reports - flagged)
(5, '0698765432', 3, NOW() - INTERVAL '1' DAY, true, NOW() - INTERVAL '20' DAY, NOW() - INTERVAL '1' DAY),
(6, '0611111111', 4, NOW() - INTERVAL '3' HOUR, true, NOW() - INTERVAL '25' DAY, NOW() - INTERVAL '3' HOUR),

-- More client numbers for variety
(7, '0722222222', 0, NULL, false, NOW(), NOW()),
(8, '0733333333', 1, NOW() - INTERVAL '7' DAY, false, NOW() - INTERVAL '10' DAY, NOW() - INTERVAL '7' DAY);

-- =====================================================
-- 3. APPOINTMENTS - Various statuses and scenarios
-- =====================================================
INSERT INTO sp_appointment (id, professional_id, client_name, client_phone, appointment_date, status, notes, created_at, updated_at) VALUES
-- For Alice (Restaurant)
(1, 1, 'Jean Dupont', '0123456789', NOW() + INTERVAL '2' DAY, 'SCHEDULED', 'Réservation pour 4 personnes, table près de la fenêtre', NOW() - INTERVAL '7' DAY, NOW() - INTERVAL '7' DAY),
(2, 1, 'Marie Martin', '0987654321', NOW() + INTERVAL '5' DAY, 'CONFIRMED', 'Anniversaire - commander le gâteau', NOW() - INTERVAL '5' DAY, NOW() - INTERVAL '1' DAY),
(3, 1, 'Pierre Durand', '0612345678', NOW() - INTERVAL '1' DAY, 'NO_SHOW', 'Client n''a pas confirmé puis ne s''est pas présenté', NOW() - INTERVAL '10' DAY, NOW() - INTERVAL '1' DAY),
(4, 1, 'Sophie Leclerc', '0645678901', NOW() - INTERVAL '3' DAY, 'COMPLETED', 'Réservation effectuée et terminée', NOW() - INTERVAL '15' DAY, NOW() - INTERVAL '3' DAY),
(5, 1, 'Luc Bernard', '0698765432', NOW() - INTERVAL '7' DAY, 'CANCELLED', 'Annulation 2 heures avant le service', NOW() - INTERVAL '20' DAY, NOW() - INTERVAL '7' DAY),

-- For Dr. Martin (Health Professional)
(6, 2, 'Alice Lefevre', '0122222222', NOW() + INTERVAL '1' DAY, 'CONFIRMED', 'Consultation générale - renouvellement ordonnance', NOW() - INTERVAL '2' DAY, NOW() - INTERVAL '1' DAY),
(7, 2, 'Robert Chevalier', '0722222222', NOW() + INTERVAL '3' DAY, 'SCHEDULED', 'Suivi hypertension', NOW() - INTERVAL '1' DAY, NOW() - INTERVAL '1' DAY),
(8, 2, 'Nadine Rousseau', '0733333333', NOW() - INTERVAL '2' DAY, 'COMPLETED', 'Visite de suivi post-opératoire', NOW() - INTERVAL '10' DAY, NOW() - INTERVAL '2' DAY),

-- For Dentist (Health Professional)
(9, 3, 'Thomas Mercier', '0644444444', NOW() + INTERVAL '7' DAY, 'SCHEDULED', 'Détartrage et nettoyage', NOW(), NOW()),
(10, 3, 'Isabelle Petit', '0655555555', NOW() - INTERVAL '1' DAY, 'NO_SHOW', 'Patient n''a pas confirmé et absent', NOW() - INTERVAL '8' DAY, NOW() - INTERVAL '1' DAY),

-- For Garage
(11, 4, 'Michel Lambert', '0666666666', NOW() + INTERVAL '4' DAY, 'SCHEDULED', 'Révision 60000 km', NOW() - INTERVAL '3' DAY, NOW() - INTERVAL '3' DAY),
(12, 4, 'Claude Olivier', '0677777777', NOW() - INTERVAL '5' DAY, 'COMPLETED', 'Changement plaquettes de frein', NOW() - INTERVAL '15' DAY, NOW() - INTERVAL '5' DAY),

-- For Hairdresser (Coiffure Mode)
(13, 5, 'Christine Renard', '0688888888', NOW() + INTERVAL '1' DAY, 'SCHEDULED', 'Coupe et couleur', NOW(), NOW()),
(14, 5, 'Valérie Gautier', '0699999999', NOW() + INTERVAL '6' DAY, 'CONFIRMED', 'Permanente', NOW() - INTERVAL '3' DAY, NOW() - INTERVAL '1' DAY),
(15, 5, 'Sylvie Moreau', '0611111111', NOW() - INTERVAL '4' DAY, 'CANCELLED', 'Annulation de dernière minute', NOW() - INTERVAL '10' DAY, NOW() - INTERVAL '4' DAY),

-- For Beauty Institute
(16, 6, 'Margot Collet', '0700000000', NOW() + INTERVAL '8' DAY, 'SCHEDULED', 'Soin du visage complet + massage', NOW() - INTERVAL '2' DAY, NOW() - INTERVAL '2' DAY),
(17, 6, 'Romane Morand', '0711111111', NOW() - INTERVAL '2' DAY, 'COMPLETED', 'Manucure et pédicure', NOW() - INTERVAL '12' DAY, NOW() - INTERVAL '2' DAY),

-- For Veterinarian
(18, 7, 'Médor (Chien)', '0722222222', NOW() + INTERVAL '2' DAY, 'SCHEDULED', 'Vaccination annuelle', NOW() - INTERVAL '1' DAY, NOW() - INTERVAL '1' DAY),
(19, 7, 'Minou (Chat)', '0733333333', NOW() - INTERVAL '1' DAY, 'COMPLETED', 'Détartrage dentaire', NOW() - INTERVAL '20' DAY, NOW() - INTERVAL '1' DAY);

-- =====================================================
-- 4. COMMERCIAL CONTACTS - Contacts pour communication
-- =====================================================
INSERT INTO sp_commercial_contact (id, professional_id, phone_number, contact_name, email, is_active, created_at, updated_at) VALUES
(1, 1, '0123456789', 'Jean Dupont', 'jean.dupont@email.com', true, NOW() - INTERVAL '30' DAY, NOW() - INTERVAL '5' DAY),
(2, 1, '0612345678', 'Sophie Leclerc', 'sophie.l@email.com', true, NOW() - INTERVAL '25' DAY, NOW() - INTERVAL '10' DAY),
(3, 2, '0234567890', 'Carole Admin', 'carole@clinic.com', true, NOW() - INTERVAL '60' DAY, NOW()),
(4, 2, '0645678901', 'Infirmière Julie', 'julie@clinic.com', true, NOW() - INTERVAL '45' DAY, NOW() - INTERVAL '2' DAY),
(5, 3, '0345678901', 'Accueil Clinique', 'accueil@smile.com', true, NOW() - INTERVAL '50' DAY, NOW() - INTERVAL '1' DAY),
(6, 4, '0456789012', 'Garagiste Principal', 'michel.meunier@garage.com', true, NOW() - INTERVAL '40' DAY, NOW()),
(7, 5, '0567890123', 'Réception', 'contact@coiffuremode.com', true, NOW() - INTERVAL '35' DAY, NOW() - INTERVAL '3' DAY),
(8, 6, '0678901234', 'Directrice Beauté', 'luxe@institut.com', true, NOW() - INTERVAL '55' DAY, NOW() - INTERVAL '1' DAY),
(9, 7, '0789012345', 'Urgences Veto', 'urgences@cliniquevet.com', true, NOW() - INTERVAL '48' DAY, NOW());

-- =====================================================
-- 5. REPORTS - Signalements de numéros problématiques
-- =====================================================
INSERT INTO sp_report (id, reported_phone, professional_id, appointment_id, reason, additional_notes, created_at) VALUES
-- Reports from Alice (Restaurant)
(1, '0612345678', 1, 3, 'NO_SHOW', 'Client réservé mais n''est pas venu sans prévenir', NOW() - INTERVAL '1' DAY),
(2, '0645678901', 1, 4, 'LATE', 'Cliente arrivée 45 minutes en retard sans raison valide', NOW() - INTERVAL '3' DAY),
(3, '0698765432', 1, 5, 'CANCELLED_LAST_MINUTE', 'Annulation 2 heures avant le service', NOW() - INTERVAL '7' DAY),

-- Reports from Dr. Martin (Health Professional)
(4, '0655555555', 2, 10, 'NO_SHOW', 'Patient absent et non contactable', NOW() - INTERVAL '1' DAY),
(5, '0611111111', 2, NULL, 'REPEATED_MISSED', 'Deuxième absence consécutive', NOW() - INTERVAL '5' DAY),

-- Reports from Hairdresser
(6, '0611111111', 5, 15, 'CANCELLED_LAST_MINUTE', 'Annulation sans préavis 2 jours avant le RDV', NOW() - INTERVAL '4' DAY),
(7, '0699999999', 5, NULL, 'LATE', 'Retard habituel de 15-30 minutes', NOW() - INTERVAL '10' DAY);

-- =====================================================
-- 6. SMS - Messages envoyés aux clients
-- =====================================================
INSERT INTO sp_sms (id, professional_id, category, recipient_phone, message_content, sent_at, created_at) VALUES
-- Reminder SMS
(1, 1, 'REMINDER', '0123456789', 'Bonjour, rappel de votre réservation demain à 19h30 chez Le Petit Bistro. Confirmez svp. Merci', NOW() - INTERVAL '1' DAY, NOW() - INTERVAL '1' DAY),
(2, 1, 'REMINDER', '0987654321', 'Rappel: réservation samedi 20h00 pour 4 pers. Le Petit Bistro. Confirmez ici: lien', NOW() - INTERVAL '2' DAY, NOW() - INTERVAL '2' DAY),
(3, 2, 'REMINDER', '0122222222', 'Dr. Martin: consultation demain 14h. Arrivez 10 min avant. Confirmez: lien', NOW(), NOW()),

-- Promotional SMS
(4, 1, 'PROMOTIONAL', '0987654321', 'Offre spéciale: -15% ce weekend pour les réservations de groupe! Le Petit Bistro', NOW() - INTERVAL '5' DAY, NOW() - INTERVAL '5' DAY),
(5, 3, 'PROMOTIONAL', '0655555555', 'Clinique du Sourire: détartrage + blanchiment spécial janvier. -20% jusqu''au 31/01. Appelez-nous!', NOW() - INTERVAL '7' DAY, NOW() - INTERVAL '7' DAY),
(6, 5, 'PROMOTIONAL', '0699999999', 'Coiffure Mode: réductions spéciales février! Forfait coupes+couleur 20% off. RDV: lien', NOW() - INTERVAL '3' DAY, NOW() - INTERVAL '3' DAY),
(7, 6, 'PROMOTIONAL', '0700000000', 'Institut Luxe: pack "Relax" 3 soins 25% de réduction! Offre janvier uniquement. Réservez: lien', NOW() - INTERVAL '6' DAY, NOW() - INTERVAL '6' DAY),
(8, 7, 'REMINDER', '0722222222', 'Clinique Vet Léon: vaccination annuelle programmée. Apportez carnet de santé. Confirmation: lien', NOW() - INTERVAL '2' DAY, NOW() - INTERVAL '2' DAY);

-- =====================================================
-- 7. AUDIT LOGS - Historique des actions
-- =====================================================
INSERT INTO sp_audit_log (id, timestamp, professional_id, action, resource_type, resource_id, details, ip_address) VALUES
-- Alice's activities
(1, NOW() - INTERVAL '30' DAY, 1, 'LOGIN', 'USER', '1', 'Connexion', '192.168.1.100'),
(2, NOW() - INTERVAL '20' DAY, 1, 'CREATE_APPOINTMENT', 'APPOINTMENT', '1', 'Création RDV Jean Dupont', '192.168.1.100'),
(3, NOW() - INTERVAL '18' DAY, 1, 'CREATE_APPOINTMENT', 'APPOINTMENT', '2', 'Création RDV Marie Martin', '192.168.1.100'),
(4, NOW() - INTERVAL '15' DAY, 1, 'CREATE_REPORT', 'REPORT', '1', 'Signalement 0612345678 - NO_SHOW', '192.168.1.100'),
(5, NOW() - INTERVAL '10' DAY, 1, 'SEND_SMS', 'SMS', '1', 'SMS rappel à 0123456789', '192.168.1.100'),

-- Dr. Martin's activities
(6, NOW() - INTERVAL '25' DAY, 2, 'LOGIN', 'USER', '2', 'Connexion', '192.168.1.101'),
(7, NOW() - INTERVAL '22' DAY, 2, 'PROFILE_UPDATE', 'USER', '2', 'Mise à jour profil', '192.168.1.101'),
(8, NOW() - INTERVAL '18' DAY, 2, 'CREATE_APPOINTMENT', 'APPOINTMENT', '6', 'Création RDV Alice Lefevre', '192.168.1.101'),
(9, NOW() - INTERVAL '5' DAY, 2, 'CREATE_REPORT', 'REPORT', '5', 'Signalement 0655555555 - NO_SHOW', '192.168.1.101'),

-- Hairdresser activities
(10, NOW() - INTERVAL '20' DAY, 5, 'LOGIN', 'USER', '5', 'Connexion', '192.168.1.102'),
(11, NOW() - INTERVAL '15' DAY, 5, 'CREATE_APPOINTMENT', 'APPOINTMENT', '13', 'Création RDV Christine Renard', '192.168.1.102'),
(12, NOW() - INTERVAL '10' DAY, 5, 'CREATE_REPORT', 'REPORT', '6', 'Signalement 0611111111 - CANCELLED', '192.168.1.102'),
(13, NOW() - INTERVAL '5' DAY, 5, 'SEND_SMS', 'SMS', '6', 'SMS promotionnel', '192.168.1.102'),

-- Veterinarian activities
(14, NOW() - INTERVAL '18' DAY, 7, 'LOGIN', 'USER', '7', 'Connexion', '192.168.1.103'),
(15, NOW() - INTERVAL '12' DAY, 7, 'CREATE_APPOINTMENT', 'APPOINTMENT', '18', 'Création RDV vaccination Médor', '192.168.1.103'),
(16, NOW() - INTERVAL '8' DAY, 7, 'SEND_SMS', 'SMS', '8', 'SMS rappel vaccination', '192.168.1.103');