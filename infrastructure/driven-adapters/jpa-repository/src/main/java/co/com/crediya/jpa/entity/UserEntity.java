package co.com.crediya.jpa.entity;

import co.com.crediya.model.user.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento_identidad", length = 20, unique = true)
    private String documentoIdentidad;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 255)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(name = "correo_electronico", nullable = false, length = 150, unique = true)
    private String correoElectronico;

    @Column(name = "salario_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal salarioBase;

    @Column(length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role rol;
}